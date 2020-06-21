package com.ksj.eoisa.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.session.SqlSession;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

@Repository
public class FCMDAO {
	
	private static final String PROJECT_ID = "";
	private static final String SERVER_KEY = "";
	private static final String BASE_URL = "https://fcm.googleapis.com";
	private static final String BASE_URL_IID = "https://iid.googleapis.com/iid/v1/";
	private static final String FCM_SEND_ENDPOINT = "/v1/projects/" + PROJECT_ID + "/messages:send";
	private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
	private static final String[] SCOPES = { MESSAGING_SCOPE };

	@Autowired
	private SqlSession sqlSession;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String NAMESPACE_MAIN = "com.ksj.eoisa.dto.MainDTO";

	@SuppressWarnings("unchecked")
	public void pushMessaging() {
		try {
			int current = sqlSession.selectOne(NAMESPACE_MAIN + ".dealCountAll");
			TimeUnit.MILLISECONDS.sleep(60000);
			int after = sqlSession.selectOne(NAMESPACE_MAIN + ".dealCountAll");

			if(after > current) {
				HttpHeaders headers = new HttpHeaders();
				headers.add("content-type", MediaType.APPLICATION_JSON_VALUE);
				headers.add("Authorization", "Bearer " + getAccessToken());

				JSONObject notification = new JSONObject();
				notification.put("title", "어머이건사야해");
				notification.put("body", "새로운 핫딜이 등록되었습니다.");

				JSONObject fcmOptions = new JSONObject();
				fcmOptions.put("link", "https://eoisa.ml");

				JSONObject webpush = new JSONObject();
				webpush.put("fcmoptions", fcmOptions);

				JSONObject message = new JSONObject();
				message.put("topic", "new");
				message.put("notification", notification);
				message.put("webpush", webpush);

				JSONObject jsonParams = new JSONObject();
				jsonParams.put("message", message);

				HttpEntity<JSONObject> httpEntity = new HttpEntity<JSONObject>(jsonParams, headers);
				RestTemplate rt = new RestTemplate();

				ResponseEntity<String> res = rt.exchange(BASE_URL + FCM_SEND_ENDPOINT, HttpMethod.POST, httpEntity, String.class);

				if(res.getStatusCode() == HttpStatus.OK) {
					logger.info("새 핫딜 등록 알림 푸시 메시지 전송 성공");
				} else {
					logger.info("새 핫딜 등록 알림 푸시 메시지 전송 실패 : HTTP Status " + res.getStatusCode().toString());
				}
			} else {
				logger.info("새 핫딜 등록 알림 푸시 메시지 전송 실패 : 새로운 핫딜 정보 없음");
			}
		} catch(Exception e) {
			logger.error("새 핫딜 등록 알림 푸시 메시지 전송 중 예외 발생", e);
			e.printStackTrace();
		}
	}

	private String getAccessToken() throws IOException {
		GoogleCredential googleCredential = GoogleCredential.fromStream(new FileInputStream("C:\\eoisa\\project-eoisa.json")).createScoped(Arrays.asList(SCOPES));
		googleCredential.refreshToken();
		
		return googleCredential.getAccessToken();
	}

	public void manageToken(String request, String clientToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", MediaType.APPLICATION_JSON_VALUE);
		headers.add("Authorization", "key=" + SERVER_KEY);

		HttpEntity<JSONObject> httpEntity = new HttpEntity<JSONObject>(headers);
		RestTemplate rt = new RestTemplate();

		if(request.equals("subscribe")) {
			rt.exchange(BASE_URL_IID + clientToken + "/rel/topics/new", HttpMethod.POST, httpEntity, String.class);
		} else if(request.equals("unsubscribe")) {
			rt.exchange(BASE_URL_IID + clientToken + "/rel/topics/new", HttpMethod.DELETE, httpEntity, String.class);
		}
	}
	
}