package com.ksj.eoisa.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ksj.eoisa.dao.SignDAO;
import com.ksj.eoisa.dto.SignDTO;

@Service
public class SignService {

	@Autowired
	private SignDAO dao;

	@Autowired
	private JavaMailSender mailSender;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public String signupService(SignDTO dto) {
		dto.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt())); // Password Encryption
		
		return dao.signup(dto);
	}

	public Boolean verifyNicknameService(String nickname) {
		return dao.verifyNickname(nickname);
	}

	public Boolean modifyUserinfoService(SignDTO dto) {
		return dao.modifyUserinfo(dto);
	}

	public void sendAuthEmailService(String username, String uuid) {
		dao.insertEmailAuthInfo(username, uuid);

		try {
			EmailService sendMail = new EmailService(mailSender);
			sendMail.setSubject("[어머이건사야해] 회원가입 이메일 인증 메일입니다.");
			sendMail.setText(new StringBuffer()
								.append("<div style='max-width: 750px; padding: 30px; border-radius: 3px; text-align: left;'>")
								.append("<img src='https://eoisa.ml/images/logo.png' width='200px' alt='어머이건사야해 로고'>")
								.append("<p><font size='6px' color='#565a5c'><strong>이메일 인증을 위한<br>링크 주소입니다.</strong></font></p>")
								.append("<hr style='margin-top: 30px; margin-bottom: 50px;'>")
								.append("<div style='border: 1px solid #ced1cc; padding: 10px;'><h3><font color='#565a5c'>해당 <a href='https://eoisa.ml/sign/verification/email/"
										+ username + "/" + uuid
										+ "' target='_blank' style='text-decoration: none;'>링크</a>를 클릭하시면 인증 절차가 완료됩니다.</font></h3></div>")
								.append("<hr style='margin-top: 50px; margin-bottom: 30px;'></div>")
                                .toString()
            );
			sendMail.setFrom("isolet0722@gmail.com", "어머이건사야해");
			sendMail.setTo(username);
			sendMail.send();

			logger.info("회원가입 인증 이메일 전송 성공 : " + username);
		} catch(Exception e) {
			logger.error("회원가입 인증 이메일 전송 실패 : " + username, e);
			e.printStackTrace();
		}
	}

	public Boolean enableUserService(String username, String uuid) {
		return dao.enableUser(username, uuid);
	}

	public Boolean findPasswordService(String username) {
		if(dao.findPassword(username) == null) {
			return false;
		} else {
			try {
				EmailService sendMail = new EmailService(mailSender);
				sendMail.setSubject("[어머이건사야해] 임시 비밀번호가 발급되었습니다.");
				sendMail.setText(new StringBuffer()
									.append("<div style='max-width: 750px; padding: 30px; border-radius: 3px; text-align: left;'>")
									.append("<img src='https://eoisa.ml/images/logo.png' width='200px' alt='어머이건사야해 로고'>")
									.append("<p><font size='6px' color='#565a5c'><strong>임시 비밀번호가<br>발급되었습니다.</strong></font></p>")
									.append("<hr style='margin-top: 30px; margin-bottom: 50px;'>")
									.append("<div style='border: 1px solid #ced1cc; padding: 10px;'><h3><font color='#007a87'>"
											+ dao.findPassword(username) + "</font></h3></div>")
									.append("<br>")
									.append("<h4><font color='#565a5c'>로그인하신 후 회원정보수정 페이지에서 비밀번호를 반드시 변경하세요.</font></h4>")
									.append("<hr style='margin-top: 50px; margin-bottom: 30px;'></div>")
                                    .toString()
                );
				sendMail.setFrom("isolet0722@gmail.com", "어머이건사야해");
				sendMail.setTo(username);
				sendMail.send();

				logger.info("임시 비밀번호 이메일 전송 성공 : " + username);
			} catch(Exception e) {
				logger.error("임시 비밀번호 이메일 전송 실패 : " + username, e);
				e.printStackTrace();
			}

			return true;
		}
	}
	
	public String uploadProfileImageService(MultipartHttpServletRequest mpsr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		final String originalPath = "C:\\eoisa\\profile\\";
		String uploadDate = sdf.format(new Date());
		String uploadPath = originalPath + uploadDate + "/";
		String saveFilename = "";

		File dir = new File(uploadPath);
		if(!dir.exists()) dir.mkdirs();

		Iterator<String> files = mpsr.getFileNames();
		while(files.hasNext()) {
			String uploadFile = files.next();

			MultipartFile mf = mpsr.getFile(uploadFile);
			saveFilename = UUID.randomUUID().toString().replace("-", "");

			try {
				mf.transferTo(new File(uploadPath + saveFilename));
				logger.info("프로필 이미지 업로드 성공");
			} catch(Exception e) {
				logger.error("프로필 이미지 업로드 실패", e);
				e.printStackTrace();
			}
		}
		
		return "/profile/" + uploadDate + "/" + saveFilename;
	}
	
}