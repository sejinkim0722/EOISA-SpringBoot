package com.ksj.eoisa.service;

import com.ksj.eoisa.dao.FCMDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

	@Autowired
	private FCMDAO dao;

	public void pushMessagingService() {
		dao.pushMessaging();
	}

	public void manageTokenService(String request, String clientToken) {
		dao.manageToken(request, clientToken);
	}
	
}