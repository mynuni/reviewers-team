# 모든 영화,TV프로그램의 정보 #리뷰어스
##### 팀원 : 박진우(조장), 김진원, 문영석, 박성범
## Contents
1. [기획 의도](#기획-의도)
2. [설계의 주안점](#설계의-주안점)
3. [사용기술 및 개발환경](#사용기술-및-개발환경)
4. [주요기능](#주요기능)
5. [Document](#document)

___
## 기획 의도
- 여러개의 플랫폼에서 이용되는 컨텐츠들의 리뷰를 하나의 사이트에서 보고 싶다.
- 누구나 리뷰를 할 수 있어야한다.
- 국내의 리뷰 사이트들은 어딘가에 종속되어 운영된다.

___
## 설계의 주안점
1. **spring security**을 이용한 비밀번호 암호화 저장
2. 회원가입,아이디 찾기, 비밀번호 변경 시 **JavaMail**를 인증번호를 전송
4. **oAuth2.0**를 이용한 네이버, 구글 소셜 로그인 구현 
5. **open source**를 이용해 글 작성시 태그 서비스와 text editor 구현
6. **openAPI**을 이용한 다양한 영화와 TV프로그램 정보들의 활용
7. **Ajax**를 활용하여 회원 비회원 구분 및 댓글 시스템 구현
8. **autocomplete jquery**을 이용한 자동 검색어 자동 완료 기능 구현

___
## 사용기술 및 개발환경
<p align="center">
    <img src = "forRead/Skill.png", width="100%">
</p>

___

## 주요기능

**1. 메인화면**
<p align="center">
    <img src = "forRead/MainPage.png", width="100%">
</p>

**2. 회원가입**
<p align="center">
    <img src = "forRead/signup.png", width="100%">
</p>

**3. 컨텐츠 상세정보**
<p align="center">
    <img src = "forRead/moviedetail1.png", width="49%">
    <img src = "forRead/moviedetail2.png", width="49%">
</p>

**4. 컨텐츠 코멘트**
<p align="center">
    <img src = "forRead/moviedetail_comment.png", width="49%">
    <img src = "forRead/moviedetail_comment2.png", width="49%">
</p>


**5. 전체 리뷰 보기**
<p align="center">
    <img src = "forRead/contentList.png", width="100%">
</p>


**6. 검색**
<p align="center">
    <img src = "forRead/search.png", width="49%">
    <img src = "forRead/searchResult.png", width="49%">
</p>

**7. 글 작성 하기**
<p align="center">
    <img src = "forRead/리뷰 작성.png", width="49%">
    <img src = "forRead/리뷰 작성(제출).png", width="49%">
</p>

**8. 댓글 작성하기**
<p align="center">
    <img src = "forRead/comment.png", width="100%">
</p>

**9. 글 상세보기**
<p align="center">
    <img src = "forRead/getBoard.png", width="100%">
</p>


**10. 신고하기**
<p align="center">
    <img src = "forRead/report_review.png", width="100%">
</p>

**11. 마이페이지**
<p align="center">
    <img src = "forRead/mypage1.png", width="49%">
    <img src = "forRead/mypage2.png", width="49%">
</p>

**12. 회원 정보수정**
<p align="center">
    <img src = "forRead/editprofile.png", width="100%">
</p>

**13. FAQ**
<p align="center">
    <img src = "forRead/FAQ.png", width="100%">
</p>

**14. 1:1문의하기**
<p align="center">
    <img src = "forRead/inquiry.png", width="100%">
</p>

**15. 관리자**

<p align="center">
    <img src = "forRead/reporReviewtList.png", width="49%">
    <img src = "forRead/reportCommentList.png", width="49%">
</p>
<p align="center">
    <img src = "forRead/reportUser.png", width="49%">
    <img src = "forRead/endreport.png", width="49%">
</p>

___

## Document

### ClassDiagram
<p align="center">
    <img src = "forRead/board.png", width="49%">
    <img src = "forRead/User.png", width="49%">
</p>
<p align="center">
    <img src = "forRead/Class_Admin.png", width="49%">
    <img src = "forRead/Class_QnA.png", width="49%">
</p>

### UseCase
<p align="center">
    <img src = "forRead/UseCase.png", width="49%">
    <img src = "forRead/UseCaseAdmin.png", width="49%">
</p>

### ER Diagram
<p align="center">
    <img src = "forRead/erdiagram.png", width="100%">
</p>
