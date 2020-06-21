package com.ksj.eoisa.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.ksj.eoisa.dao.MainDAO;
import com.ksj.eoisa.dto.MainDTO;

@Service
public class MainService {
	
	@Autowired
	private MainDAO dao;

	public void crawlingService(MainDTO dto) {
		dao.crawling(dto);
	}

	public int pagingService(String title, int pageNum) {
		return dao.paging(title, pageNum);
	}

	public int searchPagingService(String keyword, int pageNum) {
		return dao.searchPaging(keyword, pageNum);
	}

	public int filterPagingService(int pageNum, MultiValueMap<String, List<String>> filters) {
		return dao.filterPaging(pageNum, filters);
	}

	public List<MainDTO> getDealPageService(int pageNum) {
		return dao.getDealPage(pageNum);
	}

	public List<MainDTO> getFilteredDealPageService(int pageNum, MultiValueMap<String, List<String>> filters) {
		return dao.getFilteredDealPage(pageNum, filters);
	}

	public List<MainDTO> getRankPageService() {
		return dao.getRankPage();
	}

	public List<Map<String, Integer>> getRankingDealService() {
		return dao.getRankingDeal();
	}

	public List<MainDTO> getSearchResultService(String keyword, int pageNum) {
		return dao.getSearchResult(keyword, pageNum);
	}

	public List<MainDTO> getThemePageService(String title, int pageNum) {
		return dao.getThemePage(title, pageNum);
	}

	public String manageWishlistService(MainDTO dto) {
		return dao.manageWishlist(dto);
    }
    
	public List<MainDTO> getWishlistService(String username) {
		return dao.getWishlist(username);
	}

	public String increaseViewCountService(int dealno) {
		return dao.increseViewcount(dealno);
	}
	
}