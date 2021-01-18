# 로그인한 사용자 정보 참조 방법





# 계정 인증 메일 알림 

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
                <li class="nav-item" th:class="${activeTab == 'login'}? 'active':null" sec:authorize="!isAuthenticated()">
                    <a class="nav-link" th:href="@{/login}" href="/login">로그인</a>
                </li>
                <li class="nav-item" th:class="${activeTab == 'logout'}? 'active':null" sec:authorize="isAuthenticated()">
                    <a class="nav-link" th:href="@{/logout}" href="/login">로그아웃</a>
                </li>
                <li class="nav-item" th:class="${activeTab == 'signup'}? 'active':null" sec:authorize="!isAuthenticated()">
                    <a class="nav-link" th:href="@{/signup}" href="/signup">회원 가입</a>
                </li>
                <li class="nav-item" th:class="${activeTab == 'mypage'}? 'active':null" sec:authorize="isAuthenticated()">
                    <a class="nav-link" th:href="@{'/mypage/' + ${#authentication?.name}}" href="/mypage" sec:authentication="name"></a>
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
    
    <!-- 이 부분 추가 --> 
    <div class="alert alert-warning" role="alert" th:if="${member != null && !member.emailVerified}">
        가입을 완료하려면 <a href="#" th:href="@{/check-email}" class="alert-link">계정 인증 이메일을 확인</a>하세요.
    </div>

</div>
</html>



```



**MainConroller** 생성

```java
package com.megait.myhome.controller;

import com.megait.myhome.domain.Member;
import com.megait.myhome.service.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {


    @GetMapping("/")
    public String home(@CurrentUser Member member, Model model){
        if (member != null){
            model.addAttribute(member);
        }

        return  "view/index";
    }
}

```



**MemberController** 에서 다음 핸들링 삭제

```java
    // 삭제
    @RequestMapping
    public String index() {
        return "view/index";
    }
```





**CurrentUser** 애너테이션 생성

```java
package com.megait.myhome.service;


import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : member")
public @interface CurrentUser  {

}

```





**MemberUser** 클래스 생성

```java
package com.megait.myhome.service;

import com.megait.myhome.domain.Member;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class MemberUser extends User {
    private Member member;

    public MemberUser(Member member){
        super(member.getEmail(), member.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER"))); // TODO 사용자 권한 관련
        this.member = member;
    }

}

```



**MemberService**의 **login()**수정

```java
    public void login(Member member) {
        MemberUser memberUser = new MemberUser(member);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                memberUser,   							// 이 부분 수정!
                memberUser.getMember().getPassword(),	// 이 부분 수정!
                memberUser.getAuthorities()             // 이 부분 수정!
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }
```



