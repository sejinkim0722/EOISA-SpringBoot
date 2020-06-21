package com.ksj.eoisa.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.ksj.eoisa.dto.MainDTO;

@Repository
public class MainDAO {
	
	@Autowired
	private SqlSession sqlSession;
	
	private static final String NAMESPACE_MAIN = "com.ksj.eoisa.dto.MainDTO";
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public void crawling(MainDTO dto) {
        final String TARGET_URL = "https://algumon.com";
		int maxPage = sqlSession.selectOne(NAMESPACE_MAIN + ".totalPage");

		for(int page=0; page<maxPage + 1; page++) {
			try {
				long timelapseStart = System.currentTimeMillis();

				Document doc = Jsoup.connect(TARGET_URL + "/more/" + page + "?types=ended").maxBodySize(0).get();
				Elements body = doc.select("li.left.clearfix.post-li");
				for(Element el : body) {
					dto.setIsended(el.select("span.label.end").text());
					dto.setGoods_title(el.select("p > span.item-name > a").text());
					dto.setGoods_pic(el.select("div.product-img-box > a > img").attr("src"));
					dto.setUrl_src(Jsoup.connect(TARGET_URL + el.select("p > span.item-name > a").attr("href"))
										.followRedirects(true)
										.execute()
										.url()
										.toString());
					dto.setWritetime(el.select("small.label-time").first().text());
					dto.setSite_buy(el.select("span.label.shop > a").text());
					dto.setSite_src(el.select("span.label.site").first().text());
					dto.setRegion(el.select("p > small.product-price").text().matches("([$]|[£]|[¥]|[€]).*") ? "해외" : "국내");
					dto.setCategory("unknown");

					Elements dealinfo = el.select("span.deal-info");
					String[] temp = dealinfo.text().split(" ");
					String[] split = { "0", "0", "0" };
					for(int i=0; i<temp.length; i++) {
						split[i] = temp[i];
					}
					if(temp[0].trim().equals("")) {
						split[0] = "0";
					}
					dto.setReplycount_src(Integer.parseInt(split[0]));
					dto.setLikeit_src(Integer.parseInt(split[1]));
					dto.setDislikeit_src(Integer.parseInt(split[2]));

					String dealPrice = el.select("p > small.product-price").text();
					String naverPrice = getNaverPrice(el.select("p > span.item-name > a").text());
					if(dto.getRegion().equals("국내") && (!dealPrice.equals("") && !naverPrice.equals("정보 없음"))) {
						int tempDealPrice = Integer.parseInt(dealPrice.trim().replaceAll("[^0-9]", ""));
						int tempNaverPrice = Integer.parseInt(naverPrice.trim().replaceAll("[^0-9]", ""));
						dto.setMerit(String.format("%,d", (tempNaverPrice - tempDealPrice)));
					} else {
						dto.setMerit("");
					}
					dto.setPrice(dealPrice);
					dto.setPrice_naver(naverPrice);
					dto.setDeliever_fee(el.select("p > small.product-shipping-fee").text());
					
					sqlSession.update(NAMESPACE_MAIN + ".upsertDeal", dto); // DEALINFO Upsert
				}
				long timelapseEnd = System.currentTimeMillis();
				
				logger.info("크롤링 성공 : Page " + page + "(" + (timelapseEnd - timelapseStart) + " ms)");
			} catch(Exception e) {
				logger.error("크롤링 중 예외 발생", e);
				e.printStackTrace();
			}
		}
	}

	private String getNaverPrice(String goodsTitle) {
        final String CLIENT_ID = "";
        final String CLIENT_SECRET = "";

		try {
			String query = URLEncoder.encode(goodsTitle.replaceAll("[\\p{S}\\p{P}]+", "")
			    .replaceAll("(끌올|할인|청구시|청구|가성비|품절|배송비|배송|합배용|합배|관세|관부가세|적용|적용시|포함|쿠폰|포인트|무료|결제|적립|적립금|소진|특가|추가|강추|다양|이상|이하|이외|이내|국내|해외|최대|최소|NH|신한|KB|카드|국민|스마일클럽|유니온페이)", ""), "UTF-8");
			URL url = new URL("https://openapi.naver.com/v1/search/shop.json?query=" + query);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
			conn.setRequestProperty("X-Naver-Client-Secret", CLIENT_SECRET);
			
			int responseCode = conn.getResponseCode();
			BufferedReader br;
			if(responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			JsonElement jelement = new JsonParser().parse(br);
			JsonObject jobject = jelement.getAsJsonObject();
			JsonArray jarray = jobject.getAsJsonArray("items");
			jobject = jarray.get(0).getAsJsonObject();
			int result = jobject.get("lprice").getAsInt();

			br.close();
			conn.disconnect();

			logger.info("네이버쇼핑 API 최저가 조회 성공 : " + URLDecoder.decode(query, "UTF-8"));
			return String.format("%,d", result);
		} catch(Exception e) {
			return "정보 없음";
		}
	}

	public int paging(String title, int pageNum) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("title", title);
		int count = sqlSession.selectOne(NAMESPACE_MAIN + ".dealCount", params);

		return getTotalPage(count, pageNum);
	}

	public int searchPaging(String keyword, int pageNum) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("keyword", "%" + keyword + "%");
		int count = sqlSession.selectOne(NAMESPACE_MAIN + ".dealCount", params);

		return getTotalPage(count, pageNum);
	}

	public int filterPaging(int pageNum, MultiValueMap<String, List<String>> filters) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("region", filters.get("region"));
		params.put("site", filters.get("site"));
		params.put("shop", filters.get("shop"));
		params.put("isended", filters.get("isended"));
		int count = sqlSession.selectOne(NAMESPACE_MAIN + ".dealCount", params);

		return getTotalPage(count, pageNum);
	}

	private int getTotalPage(int count, int pageNum) {
		int totalPage = count / 10;
		if(count % 10 > 0) totalPage++;
		if(totalPage < pageNum) pageNum = totalPage;

		return totalPage;
	}

	public List<MainDTO> getDealPage(int pageNum) {
		HashMap<String, Integer> params = new HashMap<String, Integer>();
		params.put("startRownum", (pageNum - 1) * 10);
		params.put("endRownum", ((pageNum - 1) * 10) + 10);

		return sqlSession.selectList(NAMESPACE_MAIN + ".dealPage", params);
	}

	public List<MainDTO> getFilteredDealPage(int pageNum, MultiValueMap<String, List<String>> filters) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		List<String> rownum = Arrays.asList(Integer.toString((pageNum - 1) * 10), Integer.toString(((pageNum - 1) * 10) + 10));
		params.put("region", filters.get("region"));
		params.put("site", filters.get("site"));
		params.put("shop", filters.get("shop"));
		params.put("isended", filters.get("isended"));
		params.put("rownum", rownum);

		return sqlSession.selectList(NAMESPACE_MAIN + ".dealPage", params);
	}

	public List<MainDTO> getRankPage() {
		return sqlSession.selectList(NAMESPACE_MAIN + ".rankPage");
	}

	public List<Map<String, Integer>> getRankingDeal() {
		return sqlSession.selectList(NAMESPACE_MAIN + ".rankingDeal");
	}

	public List<MainDTO> getSearchResult(String keyword, int pageNum) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("keyword", "%" + keyword + "%");
		params.put("startRownum", Integer.toString((pageNum - 1) * 10));
		params.put("endRownum", Integer.toString(((pageNum - 1) * 10) + 10));

		logger.info("검색 성공 : keyword[" + keyword + "], page[" + pageNum + "]");
		return sqlSession.selectList(NAMESPACE_MAIN + ".dealPage", params);
	}

	public List<MainDTO> getThemePage(String title, int pageNum) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("title", title);
		params.put("startRownum", Integer.toString((pageNum - 1) * 10));
		params.put("endRownum", Integer.toString(((pageNum - 1) * 10) + 10));

		return sqlSession.selectList(NAMESPACE_MAIN + ".dealPage", params);
	}

	public String manageWishlist(MainDTO dto) {
		boolean isAlreadyAddedWishlist = (int) sqlSession.selectOne(NAMESPACE_MAIN + ".wishlistCount", dto) == 1 ? true : false;
		if(isAlreadyAddedWishlist) {
			boolean isSucceedDeleteWishlist = sqlSession.delete(NAMESPACE_MAIN + ".deleteWishlist", dto) == 1 ? true : false;

			if(isSucceedDeleteWishlist) {
				logger.info("찜 목록 삭제 성공 : dealno[" + dto.getDealno() + "], username[" + dto.getUsername() + "]");
				return "WISHLIST_DELETE_SUCCESS";
			} else {
				logger.info("찜 목록 삭제 실패 : dealno[" + dto.getDealno() + "], username[" + dto.getUsername() + "]");
				return "WISHLIST_DELETE_FAIL";
			}
		}

		int wishlistCount = (int) sqlSession.selectOne(NAMESPACE_MAIN + ".wishlistMaxCount", dto);
		if(wishlistCount == 10) {
			logger.info("찜 목록 추가 실패 : 찜 목록 가득 참(" + dto.getUsername() + ")");
			return "WISHLIST_FULL";
		} else {
			boolean isSucceedInsertWishlist = sqlSession.insert(NAMESPACE_MAIN + ".insertWishlist", dto) == 1 ? true : false;

			if(isSucceedInsertWishlist) {
				logger.info("찜 목록 추가 성공 : dealno[" + dto.getDealno() + "], username[" + dto.getUsername() + "]");
				return "WISHLIST_INSERT_SUCCESS";
			} else {
				logger.info("찜 목록 추가 실패 : dealno[" + dto.getDealno() + "], username[" + dto.getUsername() + "]");
				return "WISHLIST_INSERT_FAIL";
			}
		}
	}

	public List<MainDTO> getWishlist(String username) {
		if(!username.equals("anonymousUser")) {
			return sqlSession.selectList(NAMESPACE_MAIN + ".wishlist", username);
		} else {
			return null;
		}
	}

	public String increseViewcount(int dealno) {
		sqlSession.update(NAMESPACE_MAIN + ".increaseViewCount", dealno);

		logger.info("핫딜 조회 : " + dealno);
		return sqlSession.selectOne(NAMESPACE_MAIN + ".srcURL", dealno);
	}
	
}