package com.spring.biz.support.qna.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.biz.support.qna.FileUploadVO;
import com.spring.biz.support.qna.QnaBoardDAO;
import com.spring.biz.support.qna.QnaBoardVO;
import com.spring.biz.support.util.SearchCriteria;

@Repository
public class QnaBoardDAOImpl implements QnaBoardDAO {

	private final SqlSession sqlSession;
	private final String NAMESPACE = "com.spring.biz.support.qna.QnaBoardDAO";
	private static final Map<String, String> SUBJECT_MAP = new HashMap<>();

	static {
		SUBJECT_MAP.put("account", "계정관련");
		SUBJECT_MAP.put("content", "컨텐츠 이용");
		SUBJECT_MAP.put("error", "시스템 장애");
		SUBJECT_MAP.put("police", "신고");
		SUBJECT_MAP.put("etc", "기타");
	}

	@Autowired
	public QnaBoardDAOImpl(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	// 게시글 전체 개수 조회
	@Override
	public int getBoardCount(SearchCriteria criteria, String userId) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userId", userId);
		paramMap.put("searchType", criteria.getSearchType());
		paramMap.put("keyword", criteria.getKeyword());
		return sqlSession.selectOne(NAMESPACE + ".getBoardCount", paramMap);
	}

	// 사용자 게시글 개수 조회
	@Override
	public int getMyBoardCount(SearchCriteria criteria, String userId) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userId", userId);
		paramMap.put("searchType", criteria.getSearchType());
		paramMap.put("keyword", criteria.getKeyword());
		return sqlSession.selectOne(NAMESPACE + ".getMyBoardCount", paramMap);
	}

	// 게시글 전체 목록 조회
	@Override
	public List<QnaBoardVO> getBoardList(SearchCriteria criteria, String userId) {
		if (criteria.getSearchType() == null) {
			criteria.setSearchType("");
		}

		if (criteria.getKeyword() == null) {
			criteria.setKeyword("");
		}
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("criteria", criteria);
		paramMap.put("userId", userId);
		paramMap.put("searchType", criteria.getSearchType());
		paramMap.put("keyword", criteria.getKeyword());
		return sqlSession.selectList(NAMESPACE + ".getBoardList", criteria);
	}

	// 사용자 게시글 목록 조회
	@Override
	public List<QnaBoardVO> getMyBoardList(SearchCriteria criteria, String userId) {
		if (criteria.getSearchType() == null) {
			criteria.setSearchType("");
		}

		if (criteria.getKeyword() == null) {
			criteria.setKeyword("");
		}
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("criteria", criteria);
		paramMap.put("userId", userId);
		paramMap.put("searchType", criteria.getSearchType());
		paramMap.put("keyword", criteria.getKeyword());

		return sqlSession.selectList(NAMESPACE + ".getMyBoardList", paramMap);
	}

	// 게시글 상세보기
	@Override
	public QnaBoardVO getBoard(int boardId) {
		QnaBoardVO qnaBoardVO = sqlSession.selectOne(NAMESPACE + ".getBoard", boardId);
		String subject = qnaBoardVO.getSubject();
		String subjectKo = SUBJECT_MAP.get(subject);
		qnaBoardVO.setSubject(subjectKo);
		return qnaBoardVO;
	}
	
	// 게시글 작성
	@Override
	public QnaBoardVO insertBoard(QnaBoardVO qnaBoardVO) {
		sqlSession.insert(NAMESPACE + ".insertBoard", qnaBoardVO);
		return qnaBoardVO;
	}

	// 게시글 수정
	@Override
	public void updateBoard(QnaBoardVO qnaBoardVO) {
		sqlSession.update(NAMESPACE + ".updateBoard", qnaBoardVO);
	}

	// 게시글 삭제
	@Override
	public void deleteBoard(int boardId) {
		sqlSession.selectOne(NAMESPACE + ".deleteBoard", boardId);
	}

	// 파일 업로드
	@Override
	public void insertFileUpload(FileUploadVO fileUploadVO) {
		sqlSession.insert(NAMESPACE + ".insertFileUpload", fileUploadVO);
	}

	// 조회수 처리
	@Override
	public void calcViewCnt(int boardId) {
        sqlSession.update(NAMESPACE + ".calcViewCnt", boardId);
    }

}
