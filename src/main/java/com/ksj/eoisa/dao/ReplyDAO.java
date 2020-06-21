package com.ksj.eoisa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ksj.eoisa.dto.ReplyDTO;

@Repository
public class ReplyDAO {
	
	@Autowired
	private SqlSession sqlSession;
	
	private static final String NAMESPACE_REPLY = "com.ksj.eoisa.dto.ReplyDTO";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public boolean insertReply(ReplyDTO dto) {
		sqlSession.update(NAMESPACE_REPLY + ".increaseReplyCount", dto.getDealno());

		boolean isSucceedInsertReply = sqlSession.insert(NAMESPACE_REPLY + ".insertReply", dto) == 1 ? true : false;

		if(isSucceedInsertReply) {
			logger.info("댓글 등록 성공 : replyno[" + dto.getReplyno() + "], dealno[" + dto.getDealno() + "], username[" + dto.getUsername() + "]");
		} else {
			logger.info("댓글 등록 실패 : replyno[" + dto.getReplyno() + "], dealno[" + dto.getDealno() + "], username[" + dto.getUsername() + "]");
		}
		
		return isSucceedInsertReply;
	}

	public List<ReplyDTO> getReplylist(int dealno) {
		return sqlSession.selectList(NAMESPACE_REPLY + ".replyList", dealno);
	}

	public boolean deleteReply(int replyno) {
		sqlSession.update(NAMESPACE_REPLY + ".decreaseReplyCount", replyno);

		boolean isSucceedDeleteReply = sqlSession.delete(NAMESPACE_REPLY + ".deleteReply", replyno) == 1 ? true : false;

		if(isSucceedDeleteReply) {
			logger.info("댓글 삭제 성공 : replyno[" + replyno + "]");
		} else {
			logger.info("댓글 삭제 실패 : replyno[" + replyno + "]");
		}
		
		return isSucceedDeleteReply;
	}

	public boolean modifyReply(ReplyDTO dto) {
		boolean isSucceedModifyReply = sqlSession.update(NAMESPACE_REPLY + ".modifyReply", dto) == 1 ? true : false;

		if(isSucceedModifyReply) {
			logger.info("댓글 수정 성공 : replyno[" + dto.getReplyno() + "], dealno[" + dto.getDealno() + "], username[" + dto.getUsername() + "]");
		} else {
			logger.info("댓글 수정 실패 : replyno[" + dto.getReplyno() + "], dealno[" + dto.getDealno() + "], username[" + dto.getUsername() + "]");
		}

		return isSucceedModifyReply;
	}

	public boolean manageReplyLikeit(ReplyDTO dto) {
		int likeitCount = (int) sqlSession.selectOne(NAMESPACE_REPLY + ".likeitCount", dto);

		if(likeitCount == 1) {
			logger.info("댓글 좋아요 실패 : 이미 좋아요한 댓글(replyno[" + dto.getReplyno() + "], username[" + dto.getUsername() + "])");
			return false;
		} else {
			sqlSession.insert(NAMESPACE_REPLY + ".likeitInsert", dto);
			sqlSession.update(NAMESPACE_REPLY + ".likeitCountUp", dto);
			
			logger.info("댓글 좋아요 성공 : replyno[" + dto.getReplyno() + "], username[" + dto.getUsername() + "]");
			return true;
		}
	}
	
}