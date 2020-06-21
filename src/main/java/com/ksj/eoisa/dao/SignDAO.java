package com.ksj.eoisa.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import com.ksj.eoisa.dto.SignDTO;

@Repository
public class SignDAO {
	
	@Autowired
	private SqlSession sqlSession;
	
	private static final String NAMESPACE_SIGN = "com.ksj.eoisa.dto.SignDTO";
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public String signup(SignDTO dto) {
		Boolean isUserExist = (int) sqlSession.selectOne(NAMESPACE_SIGN + ".checkUser", dto) == 1 ? true : false;

		if(dto.getPlatform().equalsIgnoreCase("EOISA")) {
			if(isUserExist == false) {
				dto.setEnabled(0);
				sqlSession.insert(NAMESPACE_SIGN + ".insertUser", dto);

				logger.info("어이사 회원가입 성공 : " + dto.getUsername());
				return "SIGNUP_EOISA_SUCCESS";
			} else {
				logger.info("어이사 회원가입 실패 : 이미 가입된 유저가 존재함(" + dto.getUsername() + ")");
				return "USER_ALREADY_EXIST";
			}
		} else {
			if(isUserExist == false) {
				dto.setEnabled(1);
				sqlSession.insert(NAMESPACE_SIGN + ".insertUser", dto);
			}

			if(dto.getPlatform().equalsIgnoreCase("NAVER")) {
				logger.info("네이버 회원가입 성공 : " + dto.getUsername());
				return "SIGNUP_NAVER_SUCCESS";
			} else if(dto.getPlatform().equalsIgnoreCase("KAKAO")) {
				logger.info("카카오 회원가입 성공 : " + dto.getUsername());
				return "SIGNUP_KAKAO_SUCCESS";
			}
		}
		
		logger.info("회원가입 실패 : " + dto.getUsername());
		return "SIGNUP_FAIL";
	}

	public Boolean verifyNickname(String nickname) {
		return (int) sqlSession.selectOne(NAMESPACE_SIGN + ".verifyNickname", nickname) == 1 ? true : false;
	}

	public Boolean modifyUserinfo(SignDTO dto) {
		boolean isSucceedModifyUserInfo = sqlSession.update(NAMESPACE_SIGN + ".modifyUserProfile", dto) > 0 ? true : false;

		if(isSucceedModifyUserInfo) {
			logger.info("유저 정보 수정 성공 : " + dto.getUsername());
		} else {
			logger.info("유저 정보 수정 실패 : " + dto.getUsername());
		}

		return isSucceedModifyUserInfo;
	}

	public void insertEmailAuthInfo(String username, String uuid) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("uuid", uuid);

		sqlSession.insert(NAMESPACE_SIGN + ".insertEmailAuth", params);

		logger.info("이메일 인증 정보 생성 성공 : " + username + "(" + uuid + ")");
	}

	public Boolean enableUser(String username, String uuid) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("uuid", uuid);

		boolean isSucceedEnableUser = sqlSession.update(NAMESPACE_SIGN + ".enableUser", params) == 1 ? true : false;

		if(isSucceedEnableUser) {
			logger.info("유저 활성화 성공 : " + username + "(" + uuid + ")");
		} else {
			logger.info("유저 활성화 실패 : " + username + "(" + uuid + ")");
		}

		return isSucceedEnableUser;
	}

	public String findPassword(String username) {
		Boolean isUserExist = (int) sqlSession.selectOne(NAMESPACE_SIGN + ".checkUser", username) > 0 ? true : false;
		
		if(isUserExist) {
			String uuid = UUID.randomUUID().toString().replace("-", "");

			Map<String, String> params = new HashMap<String, String>();
			params.put("username", username);
			params.put("uuid", BCrypt.hashpw(uuid, BCrypt.gensalt())); // Encrypt Temp Password

			sqlSession.update(NAMESPACE_SIGN + ".updateTempPassword", params);

			logger.info("임시 비밀번호 발급 성공 : " + username);
			return uuid;
		} else {
			logger.info("임시 비밀번호 발급 실패 : 존재하지 않는 유저(" + username + ")");
			return null;
		}
	}
	
}