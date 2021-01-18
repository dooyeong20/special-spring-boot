#  로그인 및 로그아웃 구현

**SecurityConfig** 수정

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .mvcMatchers("/", "/login", "/signup", "/check-email", "/check-email-token").permitAll()
        .mvcMatchers(HttpMethod.GET, "/item/**").permitAll();

    // 이 부분 추가
    http.formLogin()
        .loginPage("/login")
        .permitAll();

    http.logout()
     // .logoutUrl("/logout") // 이 경로는 이미 기본값이다.
        .invalidateHttpSession(true)
        .logoutSuccessUrl("/");
}
```





**header.html** 

`<li>로그아웃</li>` 부분을 `<form>`으로 변경

```html
<form class="form-inline" th:action="@{/logout}" th:method="post">
    <button class="nav-item btn-dark" type="submit"
            th:class="${activeTab == 'logout'}? 'active':null" sec:authorize="isAuthenticated()">로그아웃
    </button>
</form>
```



**login.html** 수정

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="layout/common_layout" th:with="activeTab='login'">


<div layout:fragment="content">
    <div class="container">
        <main>
            <div class="py-5 text-center">
                <h2>로그인</h2>
                <p class="lead">등록된 이메일과 패스워드를 입력하세요.</p>
            </div>

            <div class="row justify-content-center">
                <div th:if="${param.error}" class="alert alert-danger" role="alert">
                    <p>이메일 혹은 패스워드가 정확하지 않습니다.</p>
                    <p>
                        <a href="#" th:href="@{/signup}">회원 가입</a>
                    </p>
                </div>
            </div>

            <div class="row g-10 justify-content-center">

                <form class="needs-validation" th:action="@{/login}" method="post" name="loginForm" novalidate>
                    <div class="row g-3">
                        <div class="col-12">
                            <label for="email" class="form-label">Email</label>
                            <input type="text" class="form-control" id="email" placeholder="you@example.com" aria-describedby="email_help_text" required>
                            <div class="invalid-feedback">필수 사항입니다.</div>
                            <small id ="email_help_text" class="form-text text-muted">
                                가입 시 사용하신 이메일을 입력하세요.
                            </small>
                        </div>
                    </div>

                    <div class="row g-3">
                        <div class="col-12">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" class="form-control" id="password" aria-describedby="password_help_text" required>
                            <div class="invalid-feedback">필수 사항입니다.</div>
                            <small id ="password_help_text" class="form-text text-muted">
                                패스워드가 기억나지 않으세요? <a href="#" th:href="@{/change-password}">패스워드 변경 링크 보내기</a><!-- TODO find-password 구현하기-->
                            </small>
                        </div>
                    </div>
                    <hr class="col-12 my-4">
                    <button class="w-50 btn btn-dark btn-lg" type="submit">Login</button>
                </form>
            </div>

        </main>
        <footer class="my-5 pt-5">

        </footer>
    </div>

</div>



</html>
```



**MemberService**에 다음 메서드 추가

```java
@Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(email);
    if(member == null){
        throw new UsernameNotFoundException(email);
    }

    return new MemberUser(member);
}
```

