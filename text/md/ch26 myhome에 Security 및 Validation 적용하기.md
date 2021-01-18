# myhome 에 Security 적용

**build.gradle** 

- security 추가
- devtools 추가 (설정 방법 : https://velog.io/@bread_dd/Spring-Boot-Devtools)
- mail 추가

```groovy
plugins {
    id 'org.springframework.boot' version '2.3.7.RELEASE'
    id 'io.spring.dependency-management' version '1.0.10.RELEASE'
    id 'java'
    id 'war'
}

group = 'com.megait'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

repositories {
    mavenCentral()
}


dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
    testImplementation('org.springframework.boot:spring-boot-starter-test') {
        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
    }
    implementation group: 'nz.net.ultraq.thymeleaf', name: 'thymeleaf-layout-dialect'

    // lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // h2
    runtimeOnly 'com.h2database:h2'

    // data-jpa
    implementation group: 'org.springframework.boot', name: 'spring-boot-starter-data-jpa', version: '2.3.7.RELEASE'

    // validation
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // security
    implementation 'org.springframework.boot:spring-boot-starter-security'

    // devtools
    // compile group: 'org.springframework.boot', name: 'spring-boot-devtools', version: '2.3.7.RELEASE'

    // mail
    implementation 'org.springframework.boot:spring-boot-starter-mail'
}

test {
    useJUnitPlatform()
}

```



# 회원가입 뷰 구현

- GET "/signup" : member/signup.html 
- POST "/signup" : form 객체로 전달 받아 유효성(Valiation) 검증 및 DB 저장

  **참고! Entity 수정됨!!!!!!!!!!! Member, Category, Item, address 다시 확인할 것!** 



## @Configuration 추가

com.megait.myhome.config 패키지에서

**SecurityConfig 클래스 추가**

```java
package com.megait.myhome.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity   // 내가 시큐리티 설정을 직접 하겠다.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()

                // 다음 목록은 로그인 없이도 요청 가능
                .mvcMatchers("/", "/login", "/signup", "/check-email", "/check-email-token").permitAll()

                // 다음 목록은 get 만 요청 가능
                .mvcMatchers(HttpMethod.GET, "/item/*").permitAll()

//                .antMatchers("/css/**", "/images/**", "/js/**", "**/favicon.ico").permitAll()

                // 나머지 요청은 로그인 해야만 요청 가능
                .anyRequest().authenticated();

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
```



**ResourceConfig 클래스 추가**

```java
package com.megait.myhome.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class ResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/images/**",
                "/css/**",
                "/js/**")
                .addResourceLocations(
                        "classpath:/static/images/",
                        "classpath:/static/css/",
                        "classpath:/static/js/");
    }
}
```



## View 수정

- 거의 다 수정되었다고 보면 된다. (디렉토리 위치 변경 안함. 추가, 수정만 해둠)
- title, activeTab 애트리뷰트 확인
- signup.html 확인
- header.html `<a>` 오류 수정



## @Controller 

com.megait.myhome.controller 패키지 추가

**MemberController**

```java
package com.megait.myhome.controller;


import com.megait.myhome.form.SignupForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MemberController {

    @GetMapping("/signup")
    public String signupForm(Model model){
        model.addAttribute(/*"signupForm",*/ new SignupForm());
        return "view/user/signup";
    }

    @GetMapping("/login")
    public String loginForm(Model model){
        return "view/user/login";
    }

}
```



## @Test

- 먼저 브라우저에서 화면 테스트 해볼 것
- MemberController 테스트용 클래스 만들기

```java
package com.megait.myhome;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    void signupForm() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("view/user/signup"))
                .andExpect(model().attributeExists("signupForm"));

    }
}
```



## @Data (SignupForm) 추가

com.megait.myhome.form 패키지 추가 

**SignupForm.java**

```java
package com.megait.myhome.form;

import lombok.Data;

@Data
public class SignupForm {
    private String email;
    private String password;

    private String street;
    private String city;
    private String zipcode;
}

```



# 회원가입 폼 검증



## 검증 방법

- 회원가입 검증 표준은 `JSR303`
  - 값의 길이, 필수값

- 커스텀 검증
  - 이메일 중복, 닉네임 중복
  - 이메일 인증

- 클라이언트에서 검증하는 방법
- 백엔드에서 검증하는 방법 
- 도메인 레이어에서 검증하는 방법 (`Validation` 사용)
- DB 에서 검증하는 방법 



## SignupForm.java (수정)

```java
package com.megait.myhome.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SignupForm {

    @NotBlank
    @Length(min = 5, max = 40)
    @Email
    private String email;

    @NotBlank
    private String password;


    @NotBlank
    private String agreeTermsOfService;

    
    private String street;
    private String city;
    
    
    private String zipcode;
}
```





## SignupFormValidator.java (추가)

```java
package com.megait.myhome.form;

import com.megait.myhome.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class SignupFormValidator implements Validator {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignupForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupForm signupForm = (SignupForm)target;
        if(memberRepository.existsByEmail(signupForm.getEmail())){
            errors.rejectValue("email", "invalid.email", new Object[]{signupForm.getEmail()}, "이미 사용중인 이메일입니다.");
        }

    }
}

```



## MemberRepository.java (추가)

com.megait.myhome.repository 패키지 추가

```java
package com.megait.myhome.repository;

import com.megait.myhome.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional(readOnly = true)
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
}

```



## MemberController.java (수정)

```java
package com.megait.myhome.controller;


import com.megait.myhome.domain.Address;
import com.megait.myhome.domain.Member;
import com.megait.myhome.form.SignupForm;
import com.megait.myhome.form.SignupFormValidator;
import com.megait.myhome.repository.MemberRepository;
import com.megait.myhome.util.ConsoleMailSender;
import groovy.util.logging.Log;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class MemberController {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SignupFormValidator signupFormValidator;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ConsoleMailSender consoleMailSender;

    @InitBinder("signupForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signupFormValidator);
    }


    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute(/*"signupForm",*/ new SignupForm());
        return "view/user/signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@Valid /*@ModelAttribute*/ SignupForm signupForm, Errors errors) {
        if (errors.hasErrors()) {
            logger.info("!!!!!!!!!!!!!!!!");
            return "view/user/signup";
        }

//        signupFormValidator.validate(signupForm, errors); // 이 부분 대신 @InitBinder 로 Validator를 등록해주자.
        logger.info("~~~~~~~~~~~");
        Member member = Member.builder()
                .email(signupForm.getEmail())
                .password(signupForm.getPassword()) // TODO encoding 해야 함
                .address(Address.builder()
                        .city(signupForm.getCity())
                        .street(signupForm.getStreet())
                        .zip(signupForm.getZipcode())
                        .build())
                .build();
        Member newMember = memberRepository.save(member);


        // 이 부분은 이따 메일 검증에서 보자
        newMember.generateEmailCheckToken(); // TODO 토큰 생성하기
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(newMember.getEmail());
        simpleMailMessage.setSubject("My Book Store 회원 가입 인증");
        simpleMailMessage.setText("/check-email-token?token="+ newMember.getEmailCheckToken() + "&email=" + newMember.getEmail());
        consoleMailSender.send(simpleMailMessage);
        return "redirect:/";
    }


    @GetMapping("/login")
    public String loginForm(Model model) {
        return "view/user/login";
    }

}

```





# 메일 보내기

com.megait.myhome.util 패키지 추가

## **ConsoleMailSender.java** 

(local 프로파일 환경에서만 메일 보내는 척하고 console에만 출력하는 가짜 메일 sender)

```java
package com.megait.myhome.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;

@Profile("local")
@Component
@Slf4j
public class ConsoleMailSender implements JavaMailSender {
    @Override
    public MimeMessage createMimeMessage() {
        return null;
    }

    @Override
    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
        return null;
    }

    @Override
    public void send(MimeMessage mimeMessage) throws MailException {

    }

    @Override
    public void send(MimeMessage... mimeMessages) throws MailException {

    }

    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {

    }

    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {

    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        log.info(simpleMessage.getText());
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {

    }
}

```



## application.yml

```yaml
spring:
  datasource:
    url: jdbc:h2:tcp://localhost/~/test
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create
      naming:
        implicit-strategy: org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy
        physical-strategy: org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy
    properties:
      hibernate:
      format_sql: true
  devtools:
    restart:
      enabled: false
  profiles:
    active: local  # 이 부분!
logging.level:
  org.hibernate.SQL: debug
```



# @Test 구현

**build.gradle**

```groovy
...
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
    testImplementation('org.springframework.boot:spring-boot-starter-test') {
    //    exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'  // 이 부분 주석처리
    }
    implementation group: 'nz.net.ultraq.thymeleaf', name: 'thymeleaf-layout-dialect'
	
    ....
        
    // security
    implementation 'org.springframework.boot:spring-boot-starter-security' 
    testCompile('org.springframework.security:spring-security-test')  // 이 부분 추가 (csrf() 사용을 위함)
    
    ....
}
```



**MemberControllerTest**

```java
// import .... 

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean  // then() 등에 넣을 객체는 Mocking 객체여야 함
    private ConsoleMailSender consoleMailSender;

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    void signupForm() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("view/user/signup"))
                .andExpect(model().attributeExists("signupForm"));

    }

    @DisplayName("회원 가입 - 입력값 오류")
    @Test
    void signupSubmit_with_wrong_input() throws Exception {

        mockMvc.perform(post("/signup")
                .param("email", "aa")  // 잘못된 이메일 형식임
                .param("password", "admin1234")
                .param("street", "")
                .param("city", "")
                .param("zipcode", "")
                // 약관 동의도 체크 안 함
                .with(csrf())) // csrf 토큰 설정도 해야 함. (thymeleaf <form>은 csrf 토큰 파라미터가 있음) //  TODO 공부하기 : csrf란?
                .andExpect(status().isOk())
                .andExpect(view().name("view/user/signup"));

    }

    @DisplayName("회원 가입 - 입력값 정상")
    @Test
    void signupSubmit_with_correct_input() throws Exception{
        mockMvc.perform(post("/signup")
                .param("email", "admin@test.com")  // 올바른 이메일 형식
                .param("password", "admin1234")

                .param("street", "")
                .param("city", "")
                .param("zipcode", "")
                .param("agreeTermsOfService", "true")// 약관 동의도 체크 함
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        // 실제 디비에 들어갔는지도 확인
        assertTrue(memberRepository.existsByEmail("admin@test.com"));

        // 메일 송신 (실제 송신은 아님) send()가 호출 되었는지 확인
       then(consoleMailSender).should().send(any(SimpleMailMessage.class));

    }
}
```

모두 pass면 됨!



# MemberService 추가 및 리팩토링

Controller의 역할이 너무 많아짐. 리팩토링 하자!



## 진행 순서

- member controller  에 있는 이메일 송신 메소드를 따로 빼기 (private sendSignupConfirmEmail())

- member controller  에 있는 회원 추가 메소드를 따로 빼기 (private saveNewMember())

  method extract 방법 : `alter` + `enter`  > `Extract Method` > 이름 짓고 `enter`

  

- service 패키지 생성

- MemberService  클래스 추가

  - 위에서 추출한 두 메서드를 여기에 private 메서드로 옮기기
  - `MemberController`가 갖고 있던 3개의 Bean을 Service로 옮기기

  ```java
  @Autowired
  private SignupFormValidator signupFormValidator;
  
  @Autowired
  private MemberRepository memberRepository;
  
  @Autowired
  private JavaMailSender javaMailSender;
  ```
  
  -  mailsender ==> javaMailSender로 이름, 자료형 수정
  
- public 새 메서드(processNewMember()) 생성 후 Controller에 있던 다음 3 줄 옮기기

  ```java
  public void processNewMember(SignupForm signupForm) {
      // DB 에 저장
      Member newMember = saveNewMember(signupForm);
  
      // 이메일 검증 링크 보내주기 (인 척하기)
      // TODO 진짜 이메일 보내기
      newMember.generateEmailCheckToken(); // 이메일 토큰 생성 및 필드에 저장
  
      sendSignupConfirmEmail(newMember);
  }
  ```

  

## Service, Controller 결과물

**MemberService**

```java
package com.megait.myhome.service;


import com.megait.myhome.domain.Address;
import com.megait.myhome.domain.Member;
import com.megait.myhome.form.SignupForm;
import com.megait.myhome.form.SignupFormValidator;
import com.megait.myhome.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

@Service
public class MemberService {

    @Autowired
    private SignupFormValidator signupFormValidator;


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @InitBinder("signupForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signupFormValidator);
    }

    @Transactional // 이 부분 추가!
    public void processNewMember(SignupForm signupForm) {
        // DB 에 저장
        Member newMember = saveNewMember(signupForm);

        // 이메일 검증 링크 보내주기 (인 척하기)
        // TODO 진짜 이메일 보내기
        newMember.generateEmailCheckToken(); // 이메일 토큰 생성 및 필드에 저장

        sendSignupConfirmEmail(newMember);
    }



    private Member saveNewMember(SignupForm signupForm) {
        Member member = Member.builder()
                .email(signupForm.getEmail())
                .password(passwordEncoder.encode(signupForm.getPassword()))
                .address(Address.builder()
                        .zip(signupForm.getZipcode())
                        .city(signupForm.getCity())
                        .street(signupForm.getStreet())
                        .build())
                .build();
        Member newMember = memberRepository.save(member);
        return newMember;
    }

    private void sendSignupConfirmEmail(Member newMember) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newMember.getEmail());
        mailMessage.setSubject("회원가입에 감사드립니다. 딱 한 과정이 남았습니다!");
        mailMessage.setText("/check-email-token?token="
                + newMember.getEmailCheckToken()
                + "&email=" + newMember.getEmail());
        javaMailSender.send(mailMessage);
    }


}

```



**MemberController**

```java
package com.megait.myhome.controller;

import com.megait.myhome.form.SignupForm;
import com.megait.myhome.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
public class MemberController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MemberService memberService;




    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute(new SignupForm());
        return "view/user/signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@Valid SignupForm signupForm, Errors errors) {
        if (errors.hasErrors()) {
            logger.info("검증 실패.....");
            return "view/user/signup";
        }
        logger.info("검증 성공!!!");

		// 이 부분!
        memberService.processNewMember(signupForm);

        return "redirect:/";  // <== 리다이렉트
    }




    @GetMapping("/login")
    public String loginForm() {
        return "view/user/login";
    }

    @RequestMapping
    public String index() {
        return "view/index";
    }
}

```



# PasswordEncoder 적용

해싱알고리즘 종류 : bcrypt, md5, sha-1 등

스프링은 bcyrpt 가 기본 설정값

Salt란? 딕셔너리 어택 방지용 Seed 값



## 구현

**config.AppConfig** 추가

```java
package com.megait.myhome.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
```



**MemberService** 에 다음 추가 및 수정 

```java
    // 추가하기
    @Autowired
    private PasswordEncoder passwordEncoder; 

	...
    
    // 수정하기
    private Member saveNewMember(SignupForm signupForm) {
        Member member = Member.builder()
                .email(signupForm.getEmail())
                .password(passwordEncoder.encode(signupForm.getPassword())) // 이 부분!
                .address(Address.builder()
                        .zip(signupForm.getZipcode())
                        .city(signupForm.getCity())
                        .street(signupForm.getStreet())
                        .build())
                .build();
        Member newMember = memberRepository.save(member);
        return newMember;
    }

	...
```





## 테스트

@Test 에 `signupSubmit_with_correct_input()` 수정

```java
    @DisplayName("회원 가입 - 입력값 정상")
    @Test
    void signupSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/signup")
                .param("email", "admin@test.com")  // 올바른 이메일 형식
                .param("password", "admin1234")

                .param("street", "")
                .param("city", "")
                .param("zipcode", "")
                .param("agreeTermsOfService", "true")// 약관 동의도 체크 함
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

		// 이 부분 추가!
        Member member = memberRepository.findByEmail("admin@test.com");
        assertNotNull(member);
        assertNotEquals(member.getPassword(), "admin1234"); // 해싱이 잘 됐는지 확인 (해싱 전 후 값이 달라야 함)
        

        // 실제 디비에 들어갔는지도 확인
        assertTrue(memberRepository.existsByEmail("admin@test.com"));
        // 메일 송신 (실제 송신은 아님) send()가 호출 되었는지 확인
        then(consoleMailSender).should().send(any(SimpleMailMessage.class));

    }
```



**MemberRepository** 에 다음 추가

```java
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Member findByEmail(String email); // 이 부분
}
```





# 메일 검증 마무리

실제 SMTP 를 적용하는 것은 개발 후반부에. (다른 기능들 테스트 하기가 힘들기 때문)



## 토큰 검사 실행 및 결과 뷰 구현

**MemberController** 에 다음 메서드 추가

```java
    @GetMapping("/check-email-token")
    @Transactional
    public String checkEmailToken(String token, String email, Model model) {

        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            model.addAttribute("error", "wrong.email");
            return "view/user/checked-email";
        }

        if (!member.isValidToken(token)) {
            model.addAttribute("error", "wrong.token");
            return "view/user/checked-email";
        }

        member.setEmailVerified(true);
        member.setJoinedAt(LocalDateTime.now());

        model.addAttribute("email", member.getEmail());

        return "view/user/checked-email";
    }
```



**Member** 에 다음 메서드 추가

```java
    public boolean isValidToken(String token) {
        return token.equals(this.getEmailCheckToken());
    }
```





resources의 user 폴더에 **checked-email.html** 추가

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="layout/common_layout" th:with="activeTab='signup', title='Sign up'">


<div layout:fragment="content">
    <div class="py-5 text-center">
        <p class="lead">My Book Store 이메일 확인</p>

        <h2 th:if="${error == null}">
            <span th:text="${email}"></span>
            <span>"님, 가입에 감사드립니다."</span>
        </h2>


        <p th:if="${error != null}" class="alert alert-danger" role="alert">이메일 확인 링크가 정확하지 않습니다.</p>
    </div>
</div>
</html>
```



## 테스트

**MemberControllerTest**

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional  //////////////////////////// 이 부분 추가! (newMember.generateEmailCheckToken();)
public class MemberControllerTest {

    ....
        
        
	///////////////// 메서드 추가 
    @DisplayName("인증 메일 확인 - 입력값 오류")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception {
        mockMvc.perform(get("/check-email-token")
                .param("token", "qweqwe")
                .param("email", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("view/user/checked-email"));
    }

    @DisplayName("인증 메일 확인 - 입력값 정상")
    @Test
    void checkEmailToken_with_correct_input() throws Exception {
        Member member = Member.builder()
                .email("test@test.com")
                .password("1234")
                .build();

        Member newMember = memberRepository.save(member);
        newMember.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("token", newMember.getEmailCheckToken())
                .param("email", newMember.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("email"))
                .andExpect(view().name("view/user/checked-email"));
    }
    /////////////////
```



## 리팩토링

**MemberController** 수정

```java
    @GetMapping("/check-email-token")
    @Transactional
    public String checkEmailToken(String token, String email, Model model) {

        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            model.addAttribute("error", "wrong.email");
            return "view/user/checked-email";
        }

        if (!token.equals(member.getEmailCheckToken())) {
            model.addAttribute("error", "wrong.token");
            return "view/user/checked-email";
        }

        //////////////////////////
        // Before
        // setEmailVerified(true);
        // setJoinedAt(LocalDateTime.now());
        
        // After
        member.completeSignup();
		/////////////////////////
        
        model.addAttribute("email", member.getEmail());

        return "view/user/checked-email";
    }
```



**Member**에 다음 메서드 추가

```java
public void completeSignup() {
    setEmailVerified(true);
    setJoinedAt(LocalDateTime.now());
}
```



