package com.spring.biz.view.board;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.biz.board.BoardService;
import com.spring.biz.board.ReviewBoardVO;
import com.spring.biz.comment.CommentVO;
import com.spring.biz.comment.service.CommentService;
import com.spring.biz.contents.service.ContentsService;
import com.spring.biz.like.LikeVO;
import com.spring.biz.like.StarVO;
import com.spring.biz.movie.ContentsDetailVO;
import com.spring.biz.movie.ContentsVO;
import com.spring.biz.movie.SearchVO;
import com.spring.biz.user.UserService;
import com.spring.biz.user.UserVO;
import com.spring.biz.util.getContentInfo;
import com.spring.biz.util.getInfoUtil;
import com.spring.biz.util.getSearchUtil;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MovieController {
	private final BoardService boardService;
	private final CommentService commentService;
	private final ContentsService contentsService;
	private final UserService userService;
	private final getContentInfo info;

	public class SortByDate implements Comparator<ContentsVO> {
		@Override
		public int compare(ContentsVO o1, ContentsVO o2) {
			Date first = o1.getRelease_date();
			Date second = o2.getRelease_date();
			return second.compareTo(first);
		}
	}

	public class SortByLike implements Comparator<ReviewBoardVO> {
		@Override
		public int compare(ReviewBoardVO l1, ReviewBoardVO l2) {
			Integer first = l1.getLike_num();
			Integer second = l2.getLike_num();
			return second.compareTo(first);
		}
	}

	public class SortByVote implements Comparator<SearchVO> {
		@Override
		public int compare(SearchVO o1, SearchVO o2) {
			Float first = o1.getPopularity();
			Float second = o2.getPopularity();
			return second.compareTo(first);
		}
	}

	@RequestMapping({ "/", "mainpage.do" })
	public String main(@RequestParam(value = "type", defaultValue = "movie") String contents_type, Model model, ReviewBoardVO vo) {
		getInfoUtil util = new getInfoUtil();
		List<ContentsVO> release_date = util.getInfoList(contents_type);
		List<ReviewBoardVO> result = boardService.getBoardListMain(vo);

		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).getReviewPic() == null) {
				int code = result.get(i).getMoviecode();
				System.out.println(code);
				String contentType = result.get(i).getContentType();
				System.out.println(contentType);
				String temp = info.getjsonObjectInfo(contentType, code).getPoster_path();
				System.out.println(temp);
				result.get(i).setReviewPic(temp);
				System.out.println(result.get(i).getReviewPic());
			}
		}

		// List에 담긴 ContentsVO를 날짜 내림차순으로 정렬
		model.addAttribute("release_date", release_date); // 최신 공개 순
		model.addAttribute("type", contents_type);

		if (vo.getSearchCondition() == null) {
			vo.setSearchCondition("TITLE");
		}
		if (vo.getSearchKeyword() == null) {
			vo.setSearchKeyword("");
		}

		model.addAttribute("boardList", result); // Model 정보 저장

		return "index";
	}

	@RequestMapping(value = "ContentsDetail.do")
	public String Detail(@RequestParam(value = "type") String contents_type, Model mode, @RequestParam(value = "id") int contents_num, HttpSession session) {

		getContentInfo info = new getContentInfo();
		ContentsDetailVO contents = info.getInfoDetail(contents_type, contents_num);
		List<ContentsDetailVO> reco = new ArrayList<ContentsDetailVO>();

		List<ContentsDetailVO> temp = new ArrayList<ContentsDetailVO>();
		temp = info.getInfoList(contents_type);

		for (int i = 0; i < temp.size(); i++) {
			List<Integer> list1 = new ArrayList<Integer>(temp.get(i).getGenress()); // 모든 컨텐츠들의 장르를 for문을 돌면서 계속 바꿔주면서
			List<Integer> list2 = new ArrayList<Integer>(contents.getGenress()); // 상세 페이지에 출력할 컨텐츠의 장르

			// 등록되어 있는 장르가 1개일 경우
			if (list2.size() == 1) {
				list1.retainAll(list2);
				if (list1.size() == 1) {
					ContentsDetailVO vo = new ContentsDetailVO();
					vo = temp.get(i);
					reco.add(vo);
				}
				// 등록되어 있는 장르가 2개일 경우
			} else if (list2.size() == 2) {
				list1.retainAll(list2);
				if (list1.size() == 2) {
					ContentsDetailVO vo = new ContentsDetailVO();
					vo = temp.get(i);
					reco.add(vo);
				}
			} else {
				list1.retainAll(list2);
				// 적어도 겹치는 장르가 세개 이상인 경우만 출력
				if (list1.size() >= 3) {
					ContentsDetailVO vo = new ContentsDetailVO();
					vo = temp.get(i);
					reco.add(vo);
				}
			}
		}

		UserVO user_Id = (UserVO) session.getAttribute("User");
		CommentVO comment = new CommentVO();
		comment.setContents_num(contents_num);
		comment.setContents_type(contents_type);
		List<CommentVO> commentList = commentService.selectList(comment); // 해당 컨텐츠에 달린 코멘트 리스트 불러오기
		mode.addAttribute("commentList", commentList);

		List<UserVO> cmt_memberList = new ArrayList<UserVO>(); // 각각의 코멘트 작성자 정보를 불러오기
		for (int i = 0; i < commentList.size(); i++) {
			UserVO cmt_member = new UserVO();
			cmt_member = userService.findMemberById(commentList.get(i).getUserId()); // 코멘트 리스트에서 각각의 코멘트 작성자의 mem_num을
																						// 통해 상세 정보를 불러와 vo에 저장
			cmt_memberList.add(cmt_member); // List 타입으로 저장
			Integer countLike = commentService.getCountLike(commentList.get(i).getComment_num()); // 코멘트 좋아요 갯수
			if (countLike != null) {
				commentList.get(i).setCountLike(countLike); // 각각의 코멘트의 좋아요 갯수
			}
		}
		mode.addAttribute("cmt_memberList", cmt_memberList);

		if (user_Id != null) {
			StarVO star = new StarVO();
			star.setContents_num(contents_num);
			star.setContents_type(contents_type);
			star.setUserId(user_Id.getUserId());
			System.out.println("asd" + star);
			StarVO starVO = contentsService.getStar(star); // 로그인한 유저가 해당 컨텐츠를 평가했는지의 정보를 불러옴
			mode.addAttribute("starVO", starVO);

			comment.setUserId(user_Id.getUserId()); // 코멘트에 로그인한 유저가 좋아요를 눌렀는지 확인하기 위해 user_Id 셋팅
			for (int i = 0; i < commentList.size(); i++) {
				comment.setComment_num(commentList.get(i).getComment_num()); // 루프를 돌며 코멘트 넘버 값을 바꿔줌
				int checkCmtLike = commentService.checkCmtLike(comment); // 각각 모든 코멘트들을 확인
				commentList.get(i).setCheckCmtLike(checkCmtLike); // 좋아요 여부 정보를 각각 저장 ( 0 → 좋아요 안 누름 / 1 → 좋아요 누름 )
			}
			mode.addAttribute("commentList", commentList);
			CommentVO getComment = commentService.getComment(comment); // 로그인한 유저가 작성한 코멘트 정보
			int checkComment = commentService.checkComment(comment); // 해당 컨텐츠에 코멘트 작성 여부 확인

			mode.addAttribute("getComment", getComment);
			mode.addAttribute("checkComment", checkComment);

			LikeVO like = new LikeVO();
			like.setContents_num(contents_num);
			like.setContents_type(contents_type);
			like.setUserId(user_Id.getUserId());
			int check = contentsService.checkLike(like); // 보고싶어요 등록 여부 확인
			mode.addAttribute("check", check); // 1 → 등록 되어 있음 0 → 등록 되어 있지 않음
			mode.addAttribute("user_id", user_Id.getUserId());
		}
		
		mode.addAttribute("reco", reco);
		mode.addAttribute("image", info.getImages(contents_type, contents_num));
		mode.addAttribute("cast", info.getCredits(contents_type, contents_num, "cast"));
		mode.addAttribute("crew", info.getCredits(contents_type, contents_num, "crew"));
		mode.addAttribute("info", info.getjsonObjectInfo(contents_type, contents_num));
		return "/board/MovieDetail";
	}

	@RequestMapping(value = "search.do")

	public String searsch(@RequestParam(value = "SC") String searchCondition, Model model, String searchKeyword,
			ReviewBoardVO vo) {
		if (searchCondition.equals("review")) {
			getContentInfo info = new getContentInfo();
			List<ReviewBoardVO> result = boardService.getSearchReview(vo);
			Collections.sort(result, new SortByLike());
			for (int i = 0; i < result.size(); i++) {
				if (result.get(i).getReviewPic() == null) {
					int code = result.get(i).getMoviecode();
					String contentType = result.get(i).getContentType();
					String temp = info.getjsonObjectInfo(contentType, code).getPoster_path();
					result.get(i).setReviewPic(temp);
				}
			}
			model.addAttribute("result", result);
			model.addAttribute("searchname", searchCondition);
			return "board/searchReview";

		} else if (searchCondition.equals("free")) {
			List<ReviewBoardVO> result = boardService.getSearchFree(vo);
			Collections.sort(result, new SortByLike());
			model.addAttribute("result", result);
			model.addAttribute("searchname", searchCondition);
			return "board/searchReview";
		} else {

			getSearchUtil search = new getSearchUtil();
			List<SearchVO> result = search.getInfoList(searchCondition, searchKeyword);
			Collections.sort(result, new SortByVote());
			model.addAttribute("result", result);
		}
		return "/board/search";
	}

	public JSONArray autoSearch(String searchCondition, String searchKeyword, ReviewBoardVO vo) throws IOException {

		int size = 0;

		JSONArray arrayObj = new JSONArray();
		JSONObject jsonObj = null;
		ArrayList<String> resultlist = new ArrayList<String>();
		HashMap<String, Integer> resulthash = new HashMap<String, Integer>();
		System.out.println(searchCondition);
		System.out.println(searchKeyword);
		Iterator<HashMap.Entry<String, Integer>> itr = resulthash.entrySet().iterator();

		if (searchCondition.equals("movie") || searchCondition.equals("tv")) {
			getSearchUtil search = new getSearchUtil();
			List<SearchVO> result = search.getInfoList(searchCondition, searchKeyword);
			List<String> poster = new ArrayList<String>();
			Collections.sort(result, new SortByVote());

			if (result.size() >= 10) {
				size = 10;

			} else {
				size = result.size();
			}

			for (int i = 0; i < size; i++) {
				String str = result.get(i).getTitle();
				int num = result.get(i).getContents_num();
				String img = result.get(i).getPoster_path();
				jsonObj = new JSONObject();
				jsonObj.put("data", str);
				jsonObj.put("value", num);
				jsonObj.put("img", img);
				arrayObj.add(jsonObj);
			}

		} else {
			List<ReviewBoardVO> result = boardService.getSearchReview(vo);
			Collections.sort(result, new SortByLike());

			if (result.size() >= 10) {
				size = 10;

			} else {
				size = result.size();
			}

			for (int i = 0; i < size; i++) {
				String str = result.get(i).getTitle();
				resultlist.add(str);
			}
			// 뽑은 후 json파싱
			for (String str : resultlist) {
				jsonObj = new JSONObject();
				jsonObj.put("data", str);
				arrayObj.add(jsonObj);

			}
		}

		return arrayObj;

	}

	@ResponseBody
	@RequestMapping(value = "autoSearch.do", method = {
			RequestMethod.GET }, produces = "application/json; charset=utf8")
	public void autoSearch(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "SC") String searchCondition, String searchKeyword, ReviewBoardVO vo)
			throws IOException {

		String searchValue = request.getParameter("searchKeyword");
		String searchCondtion = request.getParameter("SC");
		JSONArray arrayObj = MovieController.this.autoSearch(searchCondtion, searchValue, vo);
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		pw.print(arrayObj);
		pw.flush();
		pw.close();
	}

}
