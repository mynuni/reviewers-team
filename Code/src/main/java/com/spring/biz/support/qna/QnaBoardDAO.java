package com.spring.biz.support.qna;

import java.util.List;

import com.spring.biz.support.util.SearchCriteria;

public interface QnaBoardDAO {

	// 게시글 전체 개수 조회
	public int getBoardCount(SearchCriteria criteria, String userId);

	// 사용자 게시글 개수 조회
	public int getMyBoardCount(SearchCriteria criteria, String userId);

	// 게시글 전체 목록 조회
	public List<QnaBoardVO> getBoardList(SearchCriteria criteria, String userId);

	// 사용자 게시글 목록 조회
	public List<QnaBoardVO> getMyBoardList(SearchCriteria criteria, String userId);

	// 게시글 상세보기
	public QnaBoardVO getBoard(int boardId);

	// 게시글 작성
	public QnaBoardVO insertBoard(QnaBoardVO qnaBoardVO);
	
	// 게시글 수정
	public void updateBoard(QnaBoardVO qnaBoardVO);

	// 파일 업로드
	public void insertFileUpload(FileUploadVO fileUploadVO);
	
	// 게시글 삭제
	public void deleteBoard(int boardId);

	public void calcViewCnt(int boardId);

}
