package com.ksj.eoisa.controller;

import com.ksj.eoisa.service.FCMService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableScheduling
@RequestMapping("/fcm/")
public class FCMController {
	
	@Autowired
	private FCMService service;

	@Scheduled(fixedDelay = 300000)
	public void push() {
		service.pushMessagingService();
	}

	@PostMapping(value = "/token", produces = "application/text;charset=UTF-8")
	public void manageToken(@RequestParam("request") String request, @RequestParam("token") String clientToken) {
		service.manageTokenService(request, clientToken);
	}
	
}