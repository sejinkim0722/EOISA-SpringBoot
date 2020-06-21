package com.ksj.eoisa.dao;

import java.util.HashMap;
import java.util.List;

import com.ksj.eoisa.dto.BoardDTO;
import com.ksj.eoisa.dto.MainDTO;
import com.ksj.eoisa.dto.NoticeBoardDTO;
import com.ksj.eoisa.dto.SignDTO;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class AdminDAO {
	
	@Autowired
	private SqlSession sqlSession;
	
	private static final String NAMESPACE_MAIN = "com.ksj.eoisa.dto.MainDTO";
	private static final String NAMESPACE_BOARD = "com.ksj.eoisa.dto.BoardDTO";
	private static final String NAMESPACE_NOTICE = "com.ksj.eoisa.dto.NoticeBoardDTO";
	private static final String NAMESPACE_SIGN = "com.ksj.eoisa.dto.SignDTO";

	public List<SignDTO> getUserList(String column) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("column", column);
		
		return sqlSession.selectList(NAMESPACE_SIGN + ".userList", params);
	}

	public List<NoticeBoardDTO> getNoticeList() {
		return sqlSession.selectList(NAMESPACE_NOTICE + ".noticeList");
	}

	public List<BoardDTO> getFreeList() {
		return sqlSession.selectList(NAMESPACE_BOARD + ".freeList");
	}

	public List<BoardDTO> getReviewList() {
		return sqlSession.selectList(NAMESPACE_BOARD + ".reviewList");
	}

	public List<MainDTO> getDealList() {
		return sqlSession.selectList(NAMESPACE_MAIN + ".totalDeal");
	}

	public int getUserCount() {
		return sqlSession.selectOne(NAMESPACE_SIGN + ".userCount");
	}

	public int getNoticeCount() {
		return sqlSession.selectOne(NAMESPACE_NOTICE + ".noticeCount");
	}

	public int getFreeCount() {
		return sqlSession.selectOne(NAMESPACE_BOARD + ".freeCount");
	}

	public int getReviewCount() {
		return sqlSession.selectOne(NAMESPACE_BOARD + ".reviewCount");
	}

	public int getDealCount() {
		return sqlSession.selectOne(NAMESPACE_MAIN + ".dealCount");
	}

	public int updateUser(List<SignDTO> list) {
		int count = 0;
		for (SignDTO dto : list) count += sqlSession.update(NAMESPACE_SIGN + ".modifyUserinfo", dto);

		return count;
	}

	public int delReview(List<BoardDTO> list) {
		return sqlSession.delete(NAMESPACE_BOARD + ".reviewDelete", list);
	}

	public int delFree(List<BoardDTO> list) {
		return sqlSession.delete(NAMESPACE_BOARD + ".freeDelete", list);
	}

	public int delNotice(List<NoticeBoardDTO> list) {
		return sqlSession.delete(NAMESPACE_NOTICE + ".noticeDelete", list);
	}

	public int deleteDeal(List<MainDTO> list) {
		return sqlSession.delete(NAMESPACE_MAIN + ".dealDelete", list);
	}

	public int deleteSearch(List<BoardDTO> list) {
		return sqlSession.delete(NAMESPACE_BOARD + ".boardDelete", list);
	}

	// search
	public List<SignDTO> searchMember(String sVal) {
		return sqlSession.selectList(NAMESPACE_SIGN + ".searchResult", sVal);
	}

	public List<MainDTO> searchDeal(String sVal) {
		return sqlSession.selectList(NAMESPACE_MAIN + ".searchResult", sVal);
	}

	public List<BoardDTO> searchAllBoard(String sVal) {
		return sqlSession.selectList(NAMESPACE_BOARD + ".searchResult", sVal);
	}
	
}