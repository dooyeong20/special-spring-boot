참고) 쿠키 보기 크롬 확장 프로그램 : https://chrome.google.com/webstore/detail/editthiscookie/fngmhnnpilhplaeedifhccceomclgfbg?hl=ko



참고) session timeout 설정 (application.properties / application.yml)

```properties
server.serlvet.session.timeout=30m
```

```yaml
server:
  servlet:
    session:
      timeout: 30m
```



참고) 이제 `@Autowired`는 졸업하자!! 
- @RequiredArgsConstructor

- @Autowired 대신 `private final`







# 로그인 기억하기 (RememberMe)

- 세션이 만료되더라도 로그인을 유지하는 방법 => 쿠키 
- 쿠키를 사용하여 인증 정보를 암호화하여 남겨둠. 
  - 세션이 만료된 후 접속했을 때 해당 쿠키를 받아서 인증을 실행



## 쿠키 암호화 방법

쿠키에 인증 정보를 해싱하여 담는다.



방법1. 단순 해싱 쿠키 (쓰지 말자..)

**SecurityConfig**

```java
http.rememberMe()
    .key("this is randommmmmmmmmmm!!!!!!!!!!")
    ;
```



방법2. Username + 토큰 + 시리즈 쿠키 (제일 안전)



# Username + 토큰 + 시리즈 쿠키 (제일 안전)

**SecurityConfig**의 **configure(HttpSecurity)**에 다음을 추가

```java
@Override
    protected void configure(HttpSecurity http) throws Exception {
        
        ......

        http.logout()
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/");

        // 이 부분!
        http.rememberMe()
                .userDetailsService(memberService)
                .tokenRepository(tokenRepository());
       
    }
```



`memberService` 라는 새로운 빈이 사용되었다.

상단에 추가해주자.

```java
@Configuration
@EnableWebSecurity 
@RequiredArgsConstructor // 이 부분 추가!
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 이제부터는 @Autowired 대신 생성자를 활용하자.
    private final MemberService memberService; // 이 부분 추가!
    
    ....
```





**SecurityConfig**에 새로운 Bean 추가

```java
@Bean
public PersistentTokenRepository tokenRepository() {
    JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();

    jdbcTokenRepository.setDataSource(dataSource);
    return jdbcTokenRepository;
}
```

`dataSource`라는 새로운 빈이 사용되었다.

상단에 추가해주자.

```java
@Configuration
@EnableWebSecurity 
@RequiredArgsConstructor 
public class SecurityConfig extends WebSecurityConfigurerAdapter {

   
    private final MemberService memberService; 
    
    private final DataSource dataSource; // 이 부분 추가!
    // DataSource 는 JPA 를 사용하는 경우 빈으로 등록되어있다.
    
    ....
```



JdbcTokenRepositoryImpl 로 가보면 어떤 테이블이 필요한지 확인 가능. 

그것에 맞는 테이블이 존재해야 한다.

(우리는 JPA를 사용하고 있으니 Entity 를 추가하면 된다.)



**domain.PersistentLogins** 클래스 추가

```java
package com.megait.myhome.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "persistent_logins")
@Getter @Setter
public class PersistentLogins {

    @Id
    @Column(length = 64)
    private String series;

    @Column(nullable = false,length = 64)
    private String username;

    @Column(nullable = false, length = 64)
    private String token;

    @Column(name="last_used", nullable = false, length = 64)
    private LocalDateTime lastUsed;
}

```





`로그인 유지` 체크박스를 login.html 에 추가한다.

```html
...
<form>
	.....
	
	<!-- 이 쯤에 추가! -->
    <div class="form-group form-check">
        <input type="checkbox" class="form-check-input" id="rememberMe" 
        		name = "remember-me" checked/>
        <label class="form-check-label" for="rememberMe" aria-describedby="rememberMeHelp">로그인 유지</label>
	</div>
	<!-- -------------------------------- -->
    
    <hr class="col-12 my-4">
    <button class="w-50 btn btn-dark btn-lg" type="submit">Login</button>
</form>
```



테스트를 실행해본다.

