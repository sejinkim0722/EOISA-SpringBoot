package com.ksj.eoisa.service;

import java.util.List;

import com.ksj.eoisa.dao.ReplyDAO;
import com.ksj.eoisa.dto.ReplyDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplyService {
	
	@Autowired
	private ReplyDAO dao;

	public boolean insertReplyService(ReplyDTO dto) {
		return dao.insertReply(dto);
	}

	public List<ReplyDTO> getReplylistService(int dealno) {
		return dao.getReplylist(dealno);
	}

	public boolean deleteReplyService(int replyno) {
		return dao.deleteReply(replyno);
	}

	public boolean modifyReplyService(ReplyDTO dto) {
		return dao.modifyReply(dto);
	}

	public boolean manageReplyLikeitService(ReplyDTO dto) {
		return dao.manageReplyLikeit(dto);
	}
	
}