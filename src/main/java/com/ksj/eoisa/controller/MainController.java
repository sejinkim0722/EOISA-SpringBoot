package com.ksj.eoisa.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.MultiValueMap;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.ksj.eoisa.dto.MainDTO;
import com.ksj.eoisa.service.MainService;

@RestController
@EnableAsync
@EnableScheduling
public class MainController {

	@Autowired
	private MainService service;

	private boolean isCrawingEnabled = false;

	@Async
	@Scheduled(fixedDelay = 600000)
	public void handleCrawling() {
		if(isCrawingEnabled == true) {
			MainDTO dto = new MainDTO();
			service.crawlingService(dto);
		}
	}

	@RequestMapping(value = "/config/crawling/{toggle}")
	public RedirectView handleCrawlingConfig(@PathVariable String toggle) {
		if(toggle.equalsIgnoreCase("enable")) {
			isCrawingEnabled = true;
		} else if(toggle.equalsIgnoreCase("disable")) {
			isCrawingEnabled = false;
		}

		return new RedirectView("/");
	}

	// Main Page
	@RequestMapping(value = "/")
	public ModelAndView handleDefaultMainPage() {
		return handleMainPage(1);
	}

	@RequestMapping(value = "/{pageNum}")
	public ModelAndView handleMainPage(@PathVariable int pageNum) {
		int totalPage = service.pagingService(null, pageNum);
		List<MainDTO> dealList = service.getDealPageService(pageNum);
		List<Map<String, Integer>> rankingDealList = service.getRankingDealService();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<MainDTO> wishlist = service.getWishlistService(authentication.getName());

		ModelAndView mv = new ModelAndView();
		mv.setViewName("main");
		mv.addObject("viewName", "main");
		mv.addObject("deal", dealList);
		mv.addObject("ranking", rankingDealList);
		mv.addObject("totalPage", totalPage);
		if(wishlist != null) {
			mv.addObject("wishlist", wishlist);
			mv.addObject("wishedDealnoList", wishlist.stream().map(MainDTO::getDealno).collect(Collectors.toList()));
		}

		return mv;
	}

	// Deal Site Redirect & Increase Viewcount
	@RequestMapping(value = "/deal/{dealno}")
	public RedirectView handleDealPage(@PathVariable int dealno) {
		return new RedirectView(service.increaseViewCountService(dealno));
	}

	// Filtering Page
	@RequestMapping(value = "/filter/{pageNum}")
	public ModelAndView handleFilterPage(@PathVariable int pageNum, @RequestParam MultiValueMap<String, List<String>> filters) {
		int totalPage = service.filterPagingService(pageNum, filters);
		List<MainDTO> filteredDealList = service.getFilteredDealPageService(pageNum, filters);
		List<Map<String, Integer>> rankingDealList = service.getRankingDealService();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<MainDTO> wishlist = service.getWishlistService(authentication.getName());

		ModelAndView mv = new ModelAndView();
		mv.setViewName("fragments/deal");
		mv.addObject("deal", filteredDealList);
		mv.addObject("ranking", rankingDealList);
		mv.addObject("totalPage", totalPage);
		if(wishlist != null) {
			mv.addObject("wishlist", wishlist);
			mv.addObject("wishedDealnoList", wishlist.stream().map(MainDTO::getDealno).collect(Collectors.toList()));
		}

		return mv;
	}

	// Rank Page
	@RequestMapping(value = "/rank")
	public ModelAndView handleRankPage() {
		List<MainDTO> rankpage = service.getRankPageService();
		List<Map<String, Integer>> rankingDealList = service.getRankingDealService();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<MainDTO> wishlist = service.getWishlistService(authentication.getName());

		ModelAndView mv = new ModelAndView();
		mv.setViewName("main");
		mv.addObject("deal", rankpage);
		mv.addObject("ranking", rankingDealList);
		if(wishlist != null) {
			mv.addObject("wishlist", wishlist);
			mv.addObject("wishedDealnoList", wishlist.stream().map(MainDTO::getDealno).collect(Collectors.toList()));
		}

		return mv;
	}

	// Search Page
	@RequestMapping(value = "/search/{keyword}")
	public ModelAndView handleDefaultSearchPage(@PathVariable("keyword") String keyword) throws Exception {
		return handleSearchPage(keyword, 1);
	}

	@RequestMapping(value = "/search/{keyword}/{pageNum}")
	public ModelAndView handleSearchPage(@PathVariable("keyword") String keyword, @PathVariable("pageNum") int pageNum) {
		int totalPage = service.searchPagingService(keyword, pageNum);
		List<MainDTO> searchResultList = service.getSearchResultService(keyword, pageNum);
		List<Map<String, Integer>> rankingDeal = service.getRankingDealService();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<MainDTO> wishlist = service.getWishlistService(authentication.getName());

		ModelAndView mv = new ModelAndView();
		mv.setViewName("main");
		mv.addObject("keyword", keyword);
		mv.addObject("deal", searchResultList);
		mv.addObject("ranking", rankingDeal);
		mv.addObject("totalPage", totalPage);
		if(wishlist != null) {
			mv.addObject("wishlist", wishlist);
			mv.addObject("wishedDealnoList", wishlist.stream().map(MainDTO::getDealno).collect(Collectors.toList()));
		}

		return mv;
	}

	// Theme Deal Page
	@RequestMapping(value = "/theme/{title}")
	public ModelAndView defaultTheme(@PathVariable("title") String title) throws Exception {
		return handleThemePage(title, 1);
	}

	@RequestMapping(value = "/theme/{title}/{pageNum}")
	public ModelAndView handleThemePage(@PathVariable("title") String title, @PathVariable("pageNum") int pageNum) {
		int totalPage = service.pagingService(title, pageNum);
		List<MainDTO> themePageList = service.getThemePageService(title, pageNum);
		List<Map<String, Integer>> rankingDealList = service.getRankingDealService();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<MainDTO> wishlist = service.getWishlistService(authentication.getName());

		ModelAndView mv = new ModelAndView();
		mv.setViewName("main");
		mv.addObject("deal", themePageList);
		mv.addObject("ranking", rankingDealList);
		mv.addObject("totalPage", totalPage);
		if(wishlist != null) {
			mv.addObject("wishlist", wishlist);
			mv.addObject("wishedDealnoList", wishlist.stream().map(MainDTO::getDealno).collect(Collectors.toList()));
		}

		return mv;
	}

	// Manage Wishlist
	@PostMapping(value = "/wishlist")
	public ResponseEntity<String> handleWishlist(MainDTO dto) {
		String result = service.manageWishlistService(dto);

		if(result == "WISHLIST_INSERT_SUCCESS" || result == "WISHLIST_DELETE_SUCCESS") {
			return new ResponseEntity<>("WISHLIST_SUCCESS", HttpStatus.OK);
		} else if(result == "WISHLIST_FULL") {
			return new ResponseEntity<>("WISHLIST_FULL", HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Site Info Page
	@RequestMapping(value = "/info")
	public ModelAndView handleInfoPage() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("info");
		mv.addObject("viewName", "info");
		
		return mv;
	}
	
}