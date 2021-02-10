# ignoring()

정적 리소스에 관한 처리를 security 에서 제외하는 방법



## 방법1.

- **SecurityConfig**에 다음을 추가
- 스프링 부트가 제공하는 `PathRequest`를 사용하여 정적 리소스 요청에는 시큐리티 필터가 적용되지 않게 설정
- 필터 체인을 거치지 않는다.

```java
@Override
public void configure(WebSecurity web) throws Exception {

    //web.ignoring().mvcMatchers("/리소스위치");

    web.ignoring()
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
}
```



## 방법2.

- **SecurityConfig**에 다음을 추가
- 필터 체인을 거친다.

```java
@Override
public void configure(HttpSecurity http) throws Exception {

    http.authorizeRequests()
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
        .permitAll();
}
```



정적 리소스를 제외하는 경우 : 방법1을 사용

동적 요청을 제외하는 경우 : 방법2를 사용



# WebAsyncManagerIntegrationFilter

SecurityContext는 ThreadLocal 방식!

스프링 MVC에는 Async 기능(핸들러에서 Callable을 return하는 기능)이 있다.

이때 Async로 동작하는 환경에서 SecurityContext를 타 스레드와 공유해야 하는 상황이라면

`WebAsyncManagerIntegrationFilter`가 이를 자동으로 처리한다.

- PreProcess : SecurityContext를 설정한다.
- Callable : 비록 다른 쓰레드지만 그 안에서는 동일한 SecurityContext를 참조할 수 있다.
- PostProcess : SecurityContext를 정리(Clean up)한다.



테스트 : Async 환경에서 동일한 Principal이 작업 스레드에 공유되는지 확인해보자.

## SecurityLogger 클래스 생성

```java
package com.megait.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityLogger {

    public static void log(String message){

        log.info(message);

        Thread thread = Thread.currentThread();
        log.info("Current Thread : " + thread.getName());

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal : " + principal);

    }
}
```



## SampleController

메서드 추가

```java
// WebAsyncManagerIntegrationFilter 테스트
@GetMapping("/async")
@ResponseBody
public Callable<String> async(){

    SecurityLogger.log("MVC");

    return new Callable<String>() {
        @Override
        public String call() throws Exception {
            SecurityLogger.log("Callable");
            return "Async Handler";
        }
    };
}
```





# @Async 환경에서 SecurityContext 공유

## 메인 클래스

다음 애너테이션을 선언

(좋은 방법은 아니다. 더 좋은 Async환경을 위해서라면 다른 방법을 사용하는 것을 추천)

```java
@SpringBootApplication
@EnableAsync  // 이 부분! 
public class SecurityApplication {
    ...
}
```





## SampleController

```java
@GetMapping("/async-service")
@ResponseBody
public String asyncService(){
    SecurityLogger.log("MVC, before async serice");
    sampleService.asyncService();
    SecurityLogger.log("MVC, after async serice");

    return "Async Service";
}
```



이러고 실행하면 멀티쓰레드 환경으로 실행은 되지만 NullPointerException이 발생함.

왜? Principal 이 생성되기 전에 서브 스레드였던 asyncSerivice()가 먼저 실행되기 때문.

이렇게 @EnableAsync 가 적용된 앱이라면 다음과 같이 설정한다.



## SecurityConfig

```java
@Override
public void configure(HttpSecurity http) throws Exception {
   ...
       
    SecurityContextHolder.setStrategyName(
       SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
}
```



# SecuritiyContextPersistenceFilter

SecurityContextRepository를 사용하여 기존의 SecurityContext 를 읽어오거나 초기화한다.

- 기본으로 사용하는 전략은 HTTP session 을 사용하는 방법이다.
- Spring Session과 연동하여 세션 클러스터를 구현할 수 있다.



# HeaderWriterFilter

- 응답 헤더에 시큐리티 관련 헤더를 추가해주는 역할을 담당한다.
- 기본적으로 5개의 헤더 Writer가 적용된다.
- 1. XContentTypeOptionsHeaderWriter: MIME TYPE SNIFFING ATTACK PROTECITON
     - 브라우저가 컨텐츠 타입을 알아내기위해 MIME타입을 분석하는 경우가 있음.
     - 실행할수 없는 MIME타입임에 불구하고 실행하려고 시도하는 과정에서 보안 관련 문제가 존재함.
       - 뭔가를 실행한다 -> 브라우저가 다운로드를 받는 동작 등
     - X-Content-Type-Options: nosniff 헤더가 존재하면 반드시 Content-Type으로만 랜더링 하게끔한다.
- 1. XXssProtectionHeaderWriter: 브라우저에 내장된 XSS 필터 적용
     - XSS어택을 방어해준다.
     - 모든 XSS Attack을 방어해주진 못한다.
     - X-XSS-Protection: 1; mode=block; 헤더가 존재할 경우 활성화하는 옵션이다.
     - Naver의 Lucy-Filter등을 추가 적용하는것을 추천함.
- 1. CacheControlHeadersWriter
     - Cache 사용하지 않도록 설정한다.
     - Cache를 쓰면 성능상 이점을 가져오는데 왜 ?
       - 정적인 리소스를 다룰때만 해당함.
       - 동적인 페이지에는 민감한 정보가 노출될수 있기때문에 브라우저 캐싱될경우 문제가 될 수 있음.
- 1. HstsHeaderWriter: HTTPS로만 소통하도록 강제한다.
     - https 인증서 기본 유효기간이 1년이기 때문에 1년을 기본 Default 값으로 설정해 주는 등 헤더 정보를 제공한다.
     - https 설정을 하면 헤더정보가 같이 나간다.
- 1. XFrameOptionsHeaderWriter: clickjacking방어
     - iframe 과 같은것을 활용하여 개인정보 노출을 방지한다. (clickjacking)

#### 참고

- X-Content-Type-Options:
  - https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Content-Type-Options
- Cache-Control:
  - https://www.owasp.org/index.php/Testing_for_Browser_cache_weakness_(OTG-AUTHN-006)
- X-XSS-Protection
  - https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-XSS-Protection
- Lucy-Filter
  - https://github.com/naver/lucy-xss-filter
- HSTS
  - https://cheatsheetseries.owasp.org/cheatsheets/HTTP_Strict_Transport_Security_Cheat_Sheet.html
- X-Frame-Options
  - https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Frame-Options

(출처 : https://ncucu.me/118)



# CsrfFilter

CSRF 공격을 방지하는 필더



## disable 방법 (비추)

**SecurityConfig**에서

```java
@Override
public void configure(HttpSecurity http) throws Exception{
	...
    http.csrf().disable();
}
```

