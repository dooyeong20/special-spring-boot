# 인증이 완료된 Authentication 은...

- `UsernamePasswordAuthenticationFilter`
  - 폼 인증을 처리하는 시큐리티 필터
  - 인증된 Authentication 객체를 SecurityContextHolder 에 저장
  - SecurityContextHolder.getContext().setAuthentication(authentication);
- `SecurityContextPersistenceFilter`
  - SecurityContext를 HTTP session에 캐시하여 여러 요청에서  Authentication을 공유할 수 있도록 하는 필터 객체 
  - SecurityContextRepostory를 교체하여 세션을 HTTP session이 아닌 다른 곳에 저장하는 것도 가능하다.



# Spring Security 의 Filter와 FilerChainProxy

1. WebAsyncManagerIntergrationFilter
2. SecurityContextPersistenceFilter : 요청(request)전에, SecurityContextRepository에서 받아온 정보를 SecurityContextHolder에 주입.
3. HeaderWriterFilter
4. CsrfFilter
5. LogoutFilter : Principal의 로그아웃을 담당
6. UsernamePasswordAuthenticationFilter : (로그인) 인증 과정을 담당
7. DefaultLoginPageGeneratingFilter : 사용자가 별도의 로그인 페이지를 구현하지 않은 경우, 스프링에서 기본적으로 설정한 로그인 페이지를 생성
8. DefaultLogoutPageGenerationFilter
9. BasicAuthenticationFilter : HTTP 요청의 (BASIC)인증 헤더를 처리하여 결과를 SecurityContextHolder에 저장
10. RequestCacheAwareFilter
11. SecurityContextHolderAwareRequestFilter
12. RememberMeAuthenticationFilter : SecurityContext에 인증(Authentication) 객체가 있는지 확인하고RememberMeServices를 구현한 객체의 요청이 있을 경우 Remember-Me를 인증 토큰으로하여 컨텍스트에 주입.
13. AnonymousAuthenticationFilter : SecurityContextHolder에 인증(Authentication) 객체가 있는지 확인하고, 필요한 경우 Authentication 객체를 주입.
14. SessionManagementFilter : 요청이 시작된 이후 인증된 사용자인지 확인하고, 인증된 사용자일 경우SessionAuthenticationStrategy를 호출하여 세션 고정 보호 메커니즘을 활성화하거나 여러 동시 로그인을 확인하는 것과 같은 세션 관련 활동을 수행.
15. ExceptionTranslationFilter — 필터 체인 내에서 발생(Throw)되는 모든 예외(AccessDeniedException, AuthenticationException)를 처리.
16. FilterSecurityInterceptor — HTTP 리소스의 보안 처리를 수행.

**이 모든 필터를 호출하는 것이 FilterChainProxy**

우리가 만든 SecurityConfig 의 `configure()`에 어떻게 설정했는지에 따라 필터 체인이 결정된다.



****

# AccessDecisionManager

- Access Control 결정을 내리는 인터페이스

  구현체 : 

	1. AffirmativeBased : 여러 Voter 중에 하나의 Voter가 허용하면 허용. Default.

 	2. ConsensusBased : 다수결
 	3. UnanimousBased : 만장일치 



- AccessDecisionVoter 
  - Authentication이 특정한 Object에 접근할 때 필요한 ConfigAttributes 를 만족하는지 확인
  - WebExpressionVoter : 웹 시큐리티에서 사용하는 기본 구현체. ROLE_XXXX가 매치되는지 확인
  - RoleHierachyVoter : 계층형 ROLE 지원 
    - 예) ADMIN > MANAGER > USER



## AccessDecisionManger 커스터마이징 

- 권한(Authorities)을 계층형으로 만들어보자.

### 요구사항

```java
// SecurityConfig
@Override
public void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .mvcMatchers("/", "/info", "/account/**").permitAll()
        .mvcMatchers("/admin").hasRole("ADMIN")
        .mvcMatchers("/dashboard").hasRole("USER")
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .and()
        .httpBasic();
}
```

이런 구조라고 가정해보자.

`/admin`은 ADMIN만, `/dashboard`는 USER만 접근 가능하도록 되어있다.

USER가 접근할 수 있는 요청이라면 ADMIN도 접근할 수 있도록 규정하고 싶다.



### 방법1

ADMIN에게 USER 권한을 함께 주는 방법.

이 방법은 ADMIN 계정의 ROLE이 두 개인 셈이다.

```java
// AccountService
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account account = accountRepository.findByUsername(username);
    // ADMIN 권한을 주고 싶은 Authentication 일 때..
    return User.builder()
        .username(account.getUsername())
        .password(account.getPassword())
        .roles("ADMIN", "USER") // 이렇게!
        .build();
}
```



### 방법2

AccessDecisionManager 빈을 커스터마이징하는 방법

`ADMIN > USER` 룰을 직접 지정한다.

```java
// SecurityConfig 내부

// 이 부분과...
public AccessDecisionManager accessDecisionManager(){

    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

    DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);

    WebExpressionVoter voter = new WebExpressionVoter();
    voter.setExpressionHandler(handler);

    List<AccessDecisionVoter<?>> voters = Arrays.asList(voter);


    return new AffirmativeBased(voters);
}

@Override
public void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .mvcMatchers("/", "/info", "/account/**").permitAll()
        .mvcMatchers("/admin").hasRole("ADMIN")
        .mvcMatchers("/dashboard").hasRole("USER")
        .anyRequest().authenticated()
        .accessDecisionManager(accessDecisionManager()) // 이 부분!
        .and()
        .formLogin()
        .and()
        .httpBasic();
}
```



### 방법3 

`SecurityExpressionHandler`자체를 커스터마이징하는 방법

DecisionManager와 voter까지 안가는 자체 Handler를 만들기

```java
// 이 부분과...
public SecurityExpressionHandler securityExpressionHandler(){

    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

    DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);

    return handler;
}

@Override
public void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .mvcMatchers("/", "/info", "/account/**").permitAll()
        .mvcMatchers("/admin").hasRole("ADMIN")
        .mvcMatchers("/dashboard").hasRole("USER")
        .anyRequest().authenticated()
        .expressionHandler(securityExpressionHandler()) // 이 부분!
        ;
    http.formLogin()
        .and()
        .httpBasic();
}
```



# FilterSecurityInterceptor

`AccessDecisionManager`를 사용하여 Access Control 또는 예외처리를 담당하는 필터

`FilterChainProxy`에 제일 마지막 필터로 등록된다.

