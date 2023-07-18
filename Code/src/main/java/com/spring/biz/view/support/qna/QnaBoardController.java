package com.spring.biz.view.support.qna;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spring.biz.support.qna.FileUploadService;
import com.spring.biz.support.qna.FileUploadVO;
import com.spring.biz.support.qna.QnaBoardService;
import com.spring.biz.support.qna.QnaBoardVO;
import com.spring.biz.support.qnaCmt.QnaCommentService;
import com.spring.biz.support.qnaCmt.QnaCommentVO;
import com.spring.biz.support.util.PageMaker;
import com.spring.biz.support.util.SearchCriteria;
import com.spring.biz.user.UserService;
import com.spring.biz.user.UserVO;

@Controller
@RequestMapping("support/qna")
public class QnaBoardController {

	private final QnaBoardService qnaBoardService;
	private final QnaCommentService qnaCommentService;
	private final FileUploadService fileUploadService;

	@Autowired
	public QnaBoardController(QnaBoardService qnaBoardService, QnaCommentService qnaCommentService,
			FileUploadService fileUploadService, UserService userService) {
		this.qnaBoardService = qnaBoardService;
		this.qnaCommentService = qnaCommentService;
		this.fileUploadService = fileUploadService;
	}

	// 글 목록 조회
	@GetMapping
	public String getBoardList(SearchCriteria criteria, HttpSession session, Model model) {
		UserVO user = (UserVO) session.getAttribute("User");
		if (user == null) {
			return "redirect:/";
		}

		String userId = user.getUserId();
		String role = user.getRole();
		criteria.calculateRowNum();

		PageMaker pageMaker = null;
		List<QnaBoardVO> boardList = null;

		if (role.equals("관리자")) {
			pageMaker = new PageMaker(criteria, qnaBoardService.getBoardCount(criteria, userId));
			boardList = qnaBoardService.getBoardList(criteria, userId);
		} else if (role.equals("사용자")) {
			pageMaker = new PageMaker(criteria, qnaBoardService.getMyBoardCount(criteria, userId));
			boardList = qnaBoardService.getMyBoardList(criteria, userId);
		}

		pageMaker.setCriteria(criteria);
		model.addAttribute("board", boardList);
		model.addAttribute("pageMaker", pageMaker);
		return "/support/qna/qnaBoardList";

	}

	// 글 작성 폼
	@GetMapping("/write")
	public String getWriteForm(UserVO userVO, HttpSession session, Model model) {
		addUserToModelFromSession(session, model);
		return "support/qna/qnaBoardWrite";
	}

	// 글 작성 처리
	@PostMapping("/write")
	public String insertBoard(UserVO userVO, QnaBoardVO qnaBoardVO,
			@RequestParam(required = false) List<MultipartFile> files, @RequestParam String subject,
			HttpSession session, Model model) throws Exception {
		for (MultipartFile file : files) {
			System.out.println(file.getOriginalFilename());
		}
		addUserToModelFromSession(session, model);
		qnaBoardService.insertBoard(qnaBoardVO);

		if (hasFile(files)) {
			fileUploadService.uploadFile(qnaBoardVO.getBoardId(), files);
		}

		return "redirect:/support/qna/write";
	}

	// 글 조회
	@GetMapping("{boardId}")
	public String getBoard(@PathVariable int boardId, UserVO userVO, Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		addUserToModelFromSession(session, model);

		QnaBoardVO qnaBoard = qnaBoardService.getBoard(boardId);
		List<FileUploadVO> fileUploads = fileUploadService.getFileUpload(boardId);
		QnaCommentVO qnaComment = qnaCommentService.getCommentByBoardId(boardId);

		if (qnaComment != null && qnaComment.getIsDeleted().equals("N")) {
			model.addAttribute("comment", qnaComment);
		}
		
		if (qnaBoard != null && qnaBoard.getUser() != null && qnaBoard.getUser().getProfileImg() != null) {
            String writerImg = qnaBoard.getUser().getProfileImg();
            model.addAttribute("writerImg", writerImg);
        }
		calcViewCnt(boardId, request, response);
		model.addAttribute("board", qnaBoard);
		model.addAttribute("fileUploads", fileUploads);
		

		return "support/qna/qnaBoardView";
	}

	// 글 수정 폼
	@GetMapping("{boardId}/edit")
	public String getEditForm(@PathVariable int boardId, UserVO userVO, HttpSession session, Model model) {
		addUserToModelFromSession(session, model);

		QnaBoardVO board = qnaBoardService.getBoard(boardId);
		List<FileUploadVO> fileUploads = fileUploadService.getFileUpload(boardId);
		model.addAttribute("board", board);
		model.addAttribute("fileUploads", fileUploads);

		return "/support/qna/qnaBoardEdit";
	}

	// 글 수정 처리
	@PostMapping("{boardId}/edit")
	public String editBoard(@PathVariable int boardId, UserVO userVO, QnaBoardVO qnaBoardVO,
			@RequestParam(required = false) List<MultipartFile> files, @RequestParam String subject,
			@RequestParam(required = false) List<Integer> deletedFileIds, HttpSession session, Model model)
			throws Exception {

		// 첨부파일 삭제
		if (deletedFileIds != null && !deletedFileIds.isEmpty()) {
			for (Integer deletedFileId : deletedFileIds) {
				fileUploadService.deleteFileById(deletedFileId);
			}
		}

		// 첨부파일 추가
		if (hasFile(files)) {
			fileUploadService.uploadFile(boardId, files);
		}

		qnaBoardService.updateBoard(qnaBoardVO);

		return "redirect:/support/qna/" + boardId;
	}

	// 글 삭제
	@PostMapping("{boardId}/delete")
	public String deleteBoard(@PathVariable("boardId") int boardId, HttpSession session) {
		UserVO user = (UserVO) session.getAttribute("User");
		if (user == null) {
			throw new IllegalArgumentException("로그인 해주세요.");
		}

		QnaBoardVO qnaBoardVO = qnaBoardService.getBoard(boardId);

		if (qnaBoardVO == null) {
			throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
		}

		if (qnaBoardVO.getWriterId().equals(user.getUserId()) || user.getRole().equals("관리자")) {
			qnaBoardService.deleteBoard(boardId);
			System.out.println(qnaBoardVO.getBoardId() + "번 게시글 삭제 완료");
			return "redirect:/support/qna";
		} else {
			throw new IllegalArgumentException("작성자와 관리자만 삭제할 수 있습니다.");
		}

	}

	// 조회수 처리
	private void calcViewCnt(int boardId, HttpServletRequest request, HttpServletResponse response) {
	    String cookieName = "qna_view_" + boardId;
	    boolean isViewed = false;

	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if (cookieName.equals(cookie.getName())) {
	                isViewed = true;
	                break;
	            }
	        }
	    }

	    if (!isViewed) {
	        Cookie viewCookie = new Cookie(cookieName, "true");
	        viewCookie.setMaxAge(86400); // 1일
	        viewCookie.setPath("/");
	        response.addCookie(viewCookie);

	        qnaBoardService.calcViewCnt(boardId);
	    }
	}

	// 파일 업로드 검증 메소드
	private boolean hasFile(List<MultipartFile> files) {
		return files != null && !files.isEmpty() && !files.get(0).isEmpty();
	}

	public void addUserToModelFromSession(HttpSession session, Model model) {
		UserVO user = (UserVO) session.getAttribute("User");
		model.addAttribute("user", user);
	}

}
