# 메일 검증 후 자동 로그인

**MemberController**에 다음 메서드 수정

```java
    @PostMapping("/signup")
    public String signupSubmit(@Valid SignupForm signupForm, Errors errors) {
        if (errors.hasErrors()) {
            logger.info("검증 실패.....");
            return "view/user/signup";
        }
        logger.info("검증 성공!!!");

        //////////////////////
        // 이 부분
        Member member = memberService.processNewMember(signupForm); // Member 엔티티도 수정해주자.
        memberService.login(member);
        //////////////////////

        return "redirect:/";
    }
```



**MemberService**에 다음 메서드 추가

```java
    public void login(Member member) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                member.getEmail(),
                member.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")) // TODO 사용자 권한 관련
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }
```



참고) 로그인관련 정석적인 방법 

```java
// 정석적인 방법
UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
Authentication authentication = authenticationManager.authenticate(token);
SecurityContext context = SecurityContextHolder.getContext();
context.setAuthentication(token);
```

단점 -> password 는 평문이어야 한다. 



**MemberControllerTest**에 다음 추가

```java
// 인증 메일 확인 - 입력값 오류 메서드 마지막에: 
.andExpect(unauthenticated());

//인증 메일 확인 - 입력값 정상 메서드 마지막에: 
.andExpect(authenticated());
```





****

# 뷰 구현

**build.gradle**

```groovy
// https://mvnrepository.com/artifact/org.thymeleaf.extras/thymeleaf-extras-springsecurity5
compile group: 'org.thymeleaf.extras', name: 'thymeleaf-extras-springsecurity5', version: '3.0.4.RELEASE'
```



**header.html**

```html
<html lang="ko" xmlns:th="http://www.thymeleaf.org" xmlns:sec="https://www.thymeleaf.org/extras/spring-security">
<!-- Required meta tags -->
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">


<!-- 컨텐츠 페이지 CSS 삽입 -->

<div th:fragment="headerFragment(activeTab)">

    <!-- 대문 컨테이너 -->
    <div class="jumbotron jumbotron-fluid bg-dark">
        <div class="container banner">
            <h1>&#8220;The best books… <br/>are those that tell you what you know already.&#8221;</h1>
            <p>– George Orwell</p>
        </div>
    </div>

    <!-- 네비게이션 바 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
        <a class="navbar-brand" href="/" th:href="@{/}">My Book Store</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#bestSeller">
            <span class="navbar-toggler-icon"></span>
        </button>


        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item" th:class="${activeTab == 'login'}? 'active':null"
                    sec:authorize="!isAuthenticated()">
                    <a class="nav-link" th:href="@{/login}" href="/login">로그인</a>
                </li>
                <form class="form-inline" th:action="@{/logout}" th:method="post">
                    <button class="nav-item btn-dark" type="submit"
                            th:class="${activeTab == 'logout'}? 'active':null" sec:authorize="isAuthenticated()">로그아웃
                    </button>
                </form>
                <li class="nav-item" th:class="${activeTab == 'signup'}? 'active':null"
                    sec:authorize="!isAuthenticated()">
                    <a class="nav-link" th:href="@{/signup}" href="/signup">회원 가입</a>
                </li>
                <li class="nav-item" th:class="${activeTab == 'mypage'}? 'active':null"
                    sec:authorize="isAuthenticated()">
                    <a class="nav-link" th:href="@{'/mypage/' + ${#authentication?.name}}" href="/mypage"
                       sec:authentication="name"></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/" th:href="@{/}">상품 목록</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" sec:authorize="isAuthenticated()"><i class="fas fa-shopping-cart"></i></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" sec:authorize="isAuthenticated()"><i class="fas fa-heart"></i></a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" data-toggle="dropdown" href="#"
                       aria-haspopup="true" aria-expanded="false">Category</a>
                    <div class="dropdown-menu" data-toggle="pill">
                        <a class="dropdown-item" href="#">소설/수필</a>
                        <a class="dropdown-item" href="#">지식/교육</a>
                        <a class="dropdown-item" href="#">웹툰/웹소설</a>
                        <div class="dropdown-divider"></div>
                        <a class="dropdown-item" href="#">기타</a>
                    </div>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#">About Us</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#">Contact</a>
                </li>
            </ul>
            <form class="form-inline my-2 my-lg-0" action="#">
                <input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search">
                <button class="btn btn-outline-light my-2 my-sm-0" type="submit">Search</button>
            </form>
        </div>

    </nav>
    <div class="alert alert-warning" role="alert" th:if="${member != null && !member.emailVerified}">
        가입을 완료하려면 <a href="#" th:href="@{/check-email}" class="alert-link">계정 인증 이메일을 확인</a>하세요.
    </div>

</div>
</html>


```

