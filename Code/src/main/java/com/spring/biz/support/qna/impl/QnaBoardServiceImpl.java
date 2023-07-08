package com.spring.biz.support.qna.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.biz.support.qna.QnaBoardDAO;
import com.spring.biz.support.qna.QnaBoardService;
import com.spring.biz.support.qna.QnaBoardVO;
import com.spring.biz.support.util.SearchCriteria;

@Service
public class QnaBoardServiceImpl implements QnaBoardService {

	private final QnaBoardDAO qnaBoardDAO;

	@Autowired
	public QnaBoardServiceImpl(QnaBoardDAO qnaBoardDAO) {
		this.qnaBoardDAO = qnaBoardDAO;
	}

	// 게시글 전체 개수 조회
	@Override
	public int getBoardCount(SearchCriteria criteria, String userId) {
		return qnaBoardDAO.getBoardCount(criteria, userId);
	}

	// 사용자 게시글 개수 조회
	@Override
	public List<QnaBoardVO> getBoardList(SearchCriteria criteria, String userId) {
		return qnaBoardDAO.getBoardList(criteria, userId);
	}

	// 게시글 전체 목록 조회
	@Override
	public int getMyBoardCount(SearchCriteria criteria, String userId) {
		return qnaBoardDAO.getMyBoardCount(criteria, userId);
	}

	// 사용자 게시글 목록 조회
	@Override
	public List<QnaBoardVO> getMyBoardList(SearchCriteria criteria, String userId) {
		return qnaBoardDAO.getMyBoardList(criteria, userId);
	}

	// 게시글 상세보기
	@Override
	public QnaBoardVO getBoard(int boardId) {
		return qnaBoardDAO.getBoard(boardId);
	}

	// 게시글 작성
	@Override
	public QnaBoardVO insertBoard(QnaBoardVO qnaBoardVO) {
		qnaBoardDAO.insertBoard(qnaBoardVO);
		return qnaBoardVO;
	}

	// 게시글 수정
	@Override
	public void updateBoard(QnaBoardVO qnaBoardVO) {
		qnaBoardDAO.updateBoard(qnaBoardVO);
	}

	// 게시글 삭제
	@Override
	public void deleteBoard(int boardId) {
		qnaBoardDAO.deleteBoard(boardId);
	}

	// 조회수 처리
	@Override
	public void calcViewCnt(int boardId) {
        qnaBoardDAO.calcViewCnt(boardId);
		
    }
}
