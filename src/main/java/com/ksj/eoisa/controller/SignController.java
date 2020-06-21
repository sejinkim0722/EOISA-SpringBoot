package com.ksj.eoisa.controller;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.ksj.eoisa.dto.SignDTO;
import com.ksj.eoisa.service.KakaoLoginService;
import com.ksj.eoisa.service.KakaoLoginUserinfoService;
import com.ksj.eoisa.service.NaverLoginService;
import com.ksj.eoisa.service.SignService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/sign/")
public class SignController {
	
	private NaverLoginService naverLoginService;

	@Autowired
	private void setNaverLoginService(NaverLoginService naverLoginService) {
		this.naverLoginService = naverLoginService;
	}

	@Autowired
	private SignService service;

	// Eoisa Sign Form
	@RequestMapping(value = "/form")
	public ModelAndView handleSignFormPage(@ModelAttribute("result") Object result) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("signform");
		mv.addObject("viewName", "signform");
		mv.addObject("result", result);

		return mv;
	}

	@RequestMapping(value = "/fail")
	public RedirectView handleSigninFailPage(RedirectAttributes attributes) {
		attributes.addFlashAttribute("result", "SIGNIN_FAIL");

		return new RedirectView("/sign/form");
	}

	// Eoisa Signup & Social Signin
	@PostMapping(value = "/signup")
	public ModelAndView handleSignupPage(SignDTO dto) {
		String signupResult = service.signupService(dto);
		ModelAndView mv = new ModelAndView();
		
		switch(signupResult) {
			case "SIGNUP_EOISA_SUCCESS":
				service.sendAuthEmailService(dto.getUsername(), UUID.randomUUID().toString().replace("-", ""));
				mv.setViewName("signform");
				mv.addObject("viewName", "signform");
				mv.addObject("result", signupResult);
				break;
			case "SIGNUP_NAVER_SUCCESS":
			case "SIGNUP_KAKAO_SUCCESS":
				mv.setViewName("autosign");
				mv.addObject("username", dto.getUsername());
				mv.addObject("password", "socialSignIn");
				mv.addObject("request", "signin");
				break;
			case "USER_ALREADY_EXIST":
			case "SIGNUP_FAIL":
				mv.setViewName("signform");
				mv.addObject("viewName", "signform");
				mv.addObject("result", signupResult);
				break;
		}

		return mv;
	}

	// Email Verification
	@RequestMapping(value = "/verification/email/{username}/{uuid}")
	public ModelAndView verifyUser(@PathVariable("username") String username, @PathVariable("uuid") String uuid) {
		Boolean isUserEnabled = service.enableUserService(username, uuid);

		ModelAndView mv = new ModelAndView();
		mv.setViewName("verification");

		if(isUserEnabled) {
			mv.addObject("result", "EMAIL_VERIFICATION_SUCCESS");
		} else {
			mv.addObject("result", "EMAIL_VERIFICATION_FAIL");
		}
		
		return mv;
	}

	// Nickname Verification
	@PostMapping(value = "/verification/nickname")
	public ResponseEntity<String> verifyNickname(@RequestParam String nickname) {
		Boolean isNicknameExist = service.verifyNicknameService(nickname);

		if(isNicknameExist == false) {
			return new ResponseEntity<>("NICKNAME_CHECK_SUCCESS", HttpStatus.OK);
		} else if(isNicknameExist) {
			return new ResponseEntity<>("NICKNAME_ALREADY_EXIST", HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Find Password
	@PostMapping(value = "/find/password")
	public ModelAndView findPassword(@RequestParam String username) {
		Boolean isUserExist = service.findPasswordService(username);

		ModelAndView mv = new ModelAndView();
		mv.setViewName("verification");

		if(isUserExist) {
			mv.addObject("result", "PASSWORD_VERIFICATION_SUCCESS");
		} else {
			mv.addObject("result", "PASSWORD_VERIFICATION_FAIL");
		}

		return mv;
	}

	// Kakao Signin
	@RequestMapping(value = "/oauth/kakao/signin", produces = "application/json;")
	public ModelAndView handleKakaoSignin(@RequestParam("code") String code) {
		JsonNode token = KakaoLoginService.getKakaoAccessToken(code);
		JsonNode userinfo = KakaoLoginUserinfoService.getKakaoUserInfo(token.get("access_token"));

		ModelAndView mv = new ModelAndView();
		mv.setViewName("autosign");
		mv.addObject("username", userinfo.path("id").asText());
		mv.addObject("password", "socialSignIn");
		mv.addObject("nickname", userinfo.path("properties").path("nickname").asText());
		mv.addObject("profile_pic", userinfo.path("properties").path("profile_image").asText());
		mv.addObject("platform", "KAKAO");
		mv.addObject("request", "signup");

		return mv;
	}

	// Naver Signin
	@RequestMapping("/oauth/naver/url")
	public String getNaverUrl(HttpSession session) {
		return naverLoginService.getAuthorizationUrl(session);
	}

	@RequestMapping(value = "/oauth/naver/signin", produces = "application/text;")
	public ModelAndView handleNaverSignin(@RequestParam("code") String code, @RequestParam String state, HttpSession session) throws Exception {
		OAuth2AccessToken oAuthToken = naverLoginService.getAccessToken(session, code, state);
		ObjectNode userInfo = new ObjectMapper().readValue(naverLoginService.getUserProfile(oAuthToken), ObjectNode.class);

		ModelAndView mv = new ModelAndView();
		mv.setViewName("autosign");
		mv.addObject("username", userInfo.path("response").path("id").asText());
		mv.addObject("password", "socialSignIn");
		mv.addObject("nickname", userInfo.path("response").path("nickname").asText());
		mv.addObject("profile_pic", userInfo.path("response").path("profile_image").asText());
		mv.addObject("platform", "NAVER");
		mv.addObject("request", "signup");

		return mv;
	}

	// Modify Userinfo
	@PostMapping(value = "/modify/userinfo")
	public ResponseEntity<String> modifyUserinfo(SignDTO dto) {
		if(dto.getPassword() != null && !dto.getPassword().equals("")) {
			dto.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt())); // Password Encryption
		}

		Boolean isUserinfoUpdated = service.modifyUserinfoService(dto);

		return isUserinfoUpdated ? new ResponseEntity<>("MODIFY_USERINFO_SUCCESS", HttpStatus.OK) : new ResponseEntity<>("MODIFY_USERINFO_FAIL", HttpStatus.OK);
	}

	// Profile Picture Upload
	@PostMapping(value = "/upload/profileimage")
	public String uploadProfileImage(MultipartHttpServletRequest mpsr) {
		return service.uploadProfileImageService(mpsr);
	}
	
}