package com.ksj.eoisa.service;

import com.github.scribejava.core.builder.api.DefaultApi20;

public class NaverLoginApiService extends DefaultApi20 {
	
	protected NaverLoginApiService() {}
 
 	private static class InstanceHolder {
 		private static final NaverLoginApiService INSTANCE = new NaverLoginApiService();
 	}
 	
 	public static NaverLoginApiService instance() {
 		return InstanceHolder.INSTANCE;
 	}
 	
 	@Override
 	public String getAccessTokenEndpoint() {
 		return "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code";
 	}					
 
 	@Override
 	protected String getAuthorizationBaseUrl() {
 		return "https://nid.naver.com/oauth2.0/authorize";
 	}	
 	
}