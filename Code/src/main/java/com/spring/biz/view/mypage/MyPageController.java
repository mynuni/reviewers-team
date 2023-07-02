package com.spring.biz.view.mypage;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spring.biz.board.ReviewBoardVO;
import com.spring.biz.movie.ContentsDetailVO;
import com.spring.biz.mypage.CountInfoVO;
import com.spring.biz.mypage.MyContentsService;
import com.spring.biz.mypage.MyContentsVO;
import com.spring.biz.mypage.MyStarVO;
import com.spring.biz.mypage.util.PageMaker;
import com.spring.biz.mypage.util.SearchCriteria;
import com.spring.biz.user.UserService;
import com.spring.biz.user.UserVO;
import com.spring.biz.util.getContentInfo;

@Controller
@RequestMapping("mypage")
public class MyPageController {

	private final UserService userService;
	private final MyContentsService myContentsService;

	@Autowired
	public MyPageController(UserService userService, MyContentsService myContentsService) {
		this.userService = userService;
		this.myContentsService = myContentsService;
	}

	// 마이페이지 홈
	@GetMapping
	public String mypageHome(HttpSession session, SearchCriteria criteria, Model model) {
		criteria.setPerPageNum(5);
		criteria.calculateRowNum();
		String userId = getUserIdFromSession(session);
		PageMaker pageMaker = new PageMaker(criteria, myContentsService.countAllMyLike(criteria, userId));
		getContentInfo info = new getContentInfo();

		List<MyContentsVO> myContentsList = myContentsService.getMyLikeContents(criteria, userId);
		List<ContentsDetailVO> detailList = new ArrayList<>();
		for (MyContentsVO contents : myContentsList) {
			detailList.add(info.getInfoDetail(contents.getContentsType(), contents.getContentsNum()));
		}

		List<MyStarVO> myStarList = myContentsService.getMyStarContents(criteria, userId);
		List<ContentsDetailVO> starDetailList = new ArrayList<>();
		for (MyStarVO contents : myStarList) {
			starDetailList.add(info.getInfoDetail(contents.getContents_type(), contents.getContents_num()));
		}

		List<ReviewBoardVO> myReviewList = myContentsService.getMyReviewList(criteria, userId);
		List<ContentsDetailVO> reviewDetailList = new ArrayList<>();
		for (ReviewBoardVO contents : myReviewList) {
			reviewDetailList.add(info.getInfoDetail(contents.getContentType(), contents.getMoviecode()));
		}

		CountInfoVO countInfo = myContentsService.getCountInfo(criteria, userId);
		model.addAttribute("countInfo", countInfo);
		model.addAttribute("detailList", detailList);
		model.addAttribute("myContentsList", myContentsList);
		model.addAttribute("starDetailList", starDetailList);
		model.addAttribute("myStarList", myStarList);
		model.addAttribute("myReviewList", myReviewList);
		model.addAttribute("reviewDetailList", reviewDetailList);
		model.addAttribute("pageMaker", pageMaker);

		return "mypage/mypageHome";
	}

	// 나의 관심 컨텐츠 목록
	@GetMapping("/myfavorite")
	public String getMyLikeContents(SearchCriteria criteria, HttpSession session, Model model) {
		String userId = getUserIdFromSession(session);
		criteria.calculateRowNum();

		PageMaker pageMaker = new PageMaker(criteria, myContentsService.countAllMyLike(criteria, userId));
		List<MyContentsVO> myContentsList = myContentsService.getMyLikeContents(criteria, userId);

		getContentInfo info = new getContentInfo();
		List<ContentsDetailVO> detailList = new ArrayList<>();
		for (MyContentsVO contents : myContentsList) {
			detailList.add(info.getInfoDetail(contents.getContentsType(), contents.getContentsNum()));
		}

		model.addAttribute("detailList", detailList);
		model.addAttribute("pageMaker", pageMaker);
		return "/mypage/myfavorite";
	}

	// 내가 평가한 컨텐츠 목록
	@GetMapping("/mystar")
	public String getMyStarContents(SearchCriteria criteria, HttpSession session, Model model) {
		String userId = getUserIdFromSession(session);
		criteria.calculateRowNum();

		PageMaker pageMaker = new PageMaker(criteria, myContentsService.countAllMyStar(criteria, userId));
		List<MyStarVO> myContentsList = myContentsService.getMyStarContents(criteria, userId);

		getContentInfo info = new getContentInfo();
		List<ContentsDetailVO> detailList = new ArrayList<>();
		for (MyStarVO contents : myContentsList) {
			detailList.add(info.getInfoDetail(contents.getContents_type(), contents.getContents_num()));
		}

		model.addAttribute("detailList", detailList);
		model.addAttribute("myContentsList", myContentsList);
		model.addAttribute("pageMaker", pageMaker);
		return "/mypage/mystar";
	}

	// 내가 작성한 리뷰 목록
	@GetMapping("/myreview")
	public String getMyReview(SearchCriteria criteria, HttpSession session, Model model) {
		String userId = getUserIdFromSession(session);
		criteria.calculateRowNum();

		PageMaker pageMaker = new PageMaker(criteria, myContentsService.countAllMyReview(criteria, userId));
		List<ReviewBoardVO> myReviewList = myContentsService.getMyReviewList(criteria, userId);

		getContentInfo info = new getContentInfo();
		List<ContentsDetailVO> detailList = new ArrayList<>();
		for (ReviewBoardVO contents : myReviewList) {
			detailList.add(info.getInfoDetail(contents.getContentType(), contents.getMoviecode()));
		}

		model.addAttribute("detailList", detailList);
		model.addAttribute("myReviewList", myReviewList);
		model.addAttribute("pageMaker", pageMaker);
		return "/mypage/myreview";
	}

	// 회원정보 수정 폼
	@GetMapping("/edit")
	public String editForm(HttpSession session) {
		return "/mypage/editUser";
	}

	// 회원정보 수정
	@PostMapping("/edit")
	public String edit(@ModelAttribute("user") UserVO userVO, HttpSession session) {
		UserVO user = (UserVO) session.getAttribute("User");
		userService.userEdit(userVO);

		// 세션 정보 업데이트
		UserVO updatedUser = userService.findUserById(user.getUserId());
		session.setAttribute("User", updatedUser);
		return "redirect:/mypage/edit";
	}

	// 세션에서 유저 ID 가져오기
	private String getUserIdFromSession(HttpSession session) {
	    UserVO loginUser = (UserVO) session.getAttribute("User");
	    if (loginUser != null) {
	        return loginUser.getUserId();
	    }
	    return "redirect:/";
	}

	// 프로필 사진 업로드
	@PostMapping("mypicture-upload")
	public String fileUpload(@RequestParam("uploadFile") MultipartFile file, HttpSession session) throws Exception {
		UserVO user = (UserVO) session.getAttribute("User");
		userService.uploadProfileImage(file, user);
		session.setAttribute("User", user);
		return "redirect:/mypage";
	}

}
