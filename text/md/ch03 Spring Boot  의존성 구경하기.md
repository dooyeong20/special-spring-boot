# Spring Boot  의존성 구경하기

#### terminal (`alt` + `F12`)

```
$ gradlew dependencies
```



##### spring-boot-starter-web

- spring-boot-starter-tomcat: 톰캣 (웹서버)
- spring-webmvc: 스프링 웹 MVC

##### spring-boot-starter-thymeleaf: 타임리프 템플릿 엔진(View)

##### spring-boot-starter-data-jpa

- spring-boot-starter-aop
- spring-boot-starter-jdbc
  - HikariCP 커넥션 풀 (부트 2.0 기본)
- hibernate + JPA: 하이버네이트 + JPA
- spring-data-jpa: 스프링 데이터 JPA

##### spring-boot-starter(공통): 스프링 부트 + 스프링 코어 + 로깅

- spring-boot
  - spring-core
- spring-boot-starter-logging
  - logback, slf4j



### 테스트 라이브러리

##### spring-boot-starter-test

- junit: 테스트 프레임워크
- mockito: 목 라이브러리
- assertj: 테스트 코드를 좀 더 편하게 작성하게 도와주는 라이브러리
- spring-test: 스프링 통합 테스트 지원



##### 핵심 라이브러리

- 스프링 MVC
- 스프링 ORM
- JPA, 하이버네이트
- 스프링 데이터 JPA



##### 기타 라이브러리

- H2 데이터베이스 클라이언트
- 커넥션 풀: 부트 기본은 HikariCP
- WEB(thymeleaf)
- 로깅 SLF4J & LogBack
- 테스트

