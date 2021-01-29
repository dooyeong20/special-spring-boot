# 메일 서비스

- 구글 Gmail을 SMTP 서버로 사용하기
  - https://support.google.com/mail/answer/185833
  - 관리용 메일은 2단계 인증이 되어야 함
  - gmail 이어야 함
  - app password 추천



## application.properties

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=ssellu.myhome@gmail.com  // 내 이메일
spring.mail.password=rtntecdpxiezaphl	// 내 App 패스워드
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.starttls.enable=true
```



## application.yml

```yaml
spring:
  ...
  
  mail:
    host: smtp.gmail.com
    port: 587
    username: ssellu.myhome@gmail.com  # 내 이메일
    password: rtntecdpxiezaphl	# 내 App 패스워드
    properties:
      mail:
        smtp:
          auth: true
          timeout: 5000
          starttls.enable: true
```

이렇게 설정하면 자동으로 JavaMailSender 빈이 생성됨.

ConsoleMailSender 빈을 비활성화하고 테스트



기타 SMTP

- https://sendgrid.com/
- https://www.mailgun.com/
- https://workspace.google.com/
- https://aws.amazon.com/ko/ses/



****



# 이메일 html 로 보내기



## MemberService

```java
public void sendSignupConfirmEmail(Member newMember) {
    // TODO
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    try {
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        mimeMessageHelper.setTo(newMember.getEmail());
        mimeMessageHelper.setSubject("My Book Store 회원 가입 인증");
        mimeMessageHelper.setText("/check-email-token?token="+ newMember.getEmailCheckToken() + "&email=" + newMember.getEmail(), false);
        javaMailSender.send(mimeMessage);
    } catch (MessagingException e) {
        log.error("Failed to send email.", e);
    }


    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setTo(newMember.getEmail());
    simpleMailMessage.setSubject("My Book Store 회원 가입 인증");
    simpleMailMessage.setText("/check-email-token?token="+ newMember.getEmailCheckToken() + "&email=" + newMember.getEmail());
    javaMailSender.send(simpleMailMessage);
}
```

profile에 따른 콘솔 출력용으로 내가 만든 ConsoleMailSender 도 함께 사용하고 싶다..



****

# EmailService 추상화





## EmailSerivce 추가

```java
@Service
public interface EmailService {
    void sendEmail(EmailMessage emailMessage);
}
```



## EmailMessage 추가

```java

package com.megait.myhome.util;


import lombok.Builder;
import lombok.Data;

// TODO 2
@Data
@Builder
public class EmailMessage {
    private String to;
    private String subject;
    private String message;
}

```



## ConsoleEmailService 추가

```java
package com.megait.myhome.service;

import com.megait.myhome.util.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
@Slf4j
public class ConsoleEmailService implements EmailService{
    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("email has sent: {}", emailMessage.getMessage());
    }
}

```



## HtmlEmailService 추가

```java
package com.megait.myhome.service;

import com.megait.myhome.util.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Profile("dev")
@Slf4j
@Component
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService{

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo());
            mimeMessageHelper.setSubject(emailMessage.getSubject());
            mimeMessageHelper.setText(emailMessage.getMessage(), true);
            javaMailSender.send(mimeMessage);
            log.info("Email has sent: {}", emailMessage.getMessage());
        } catch (MessagingException e) {
            log.error("Failed to send email.", e);
        }
    }
}

```



## ConsoleMailSender 클래스 삭제



## MemberService 

- `EmailSerivce` 빈 추가

```java
private final EmailService emailService
```



-  `sendSignupConfirmEmail()` 수정

```java
public void sendSignupConfirmEmail(Member newMember) {
        EmailMessage emailMessage = EmailMessage.builder()
        .to(newMember.getEmail())
        .subject("My Book Store 회원 가입 인증")
        .message("/check-email-token?token="+ newMember.getEmailCheckToken() + "&email=" + newMember.getEmail())
        .build();
    emailService.sendEmail(emailMessage);
}
```



- `sendMailResetPassword()` 수정

```java
public void sendMailResetPassword(String email) {
    // email 파람으로 findByEmail()
    Member member = memberRepository.findByEmail(email);

    // 해당 회원이 없니?
    if(member == null) {
        return;
    }

    // 거기에 emailCheckToken이 있을 것이니...
    String token = member.getEmailCheckToken();

    // token + email 조합으로 링크 만들기
    String url = "/reset-password?token=" + token + "&email=" + email;
 
    // 메일 전송
    //        SimpleMailMessage mailMessage = new SimpleMailMessage();
    //        mailMessage.setTo(email);
    //        mailMessage.setSubject("My Book Store - 비밀번호 재설정 링크입니다.");
    //        mailMessage.setText("링크 : " + url);
    //
    //        emailService.send(mailMessage);
    EmailMessage emailMessage = EmailMessage.builder()
        .to(email)
        .subject("My Book Store - 비밀번호 재설정 링크입니다.")
        .message("링크 : " + url)
        .build();
    emailService.sendEmail(emailMessage);
}
```



## MemberControllerTest

```java
    @MockBean
    private EmailService emailService; // 이 부분!

	...
        
    @DisplayName("회원 가입 - 입력값 정상")
    @Test
    void signupSubmit_with_correct_input() throws Exception {
    mockMvc.perform(post("/signup")
                    .param("email", "issell@naver.com")
                    .param("password", "1234")
                    .param("agreeTermsOfService", "true") 
                    .with(csrf()))
        .andExpect(status().is3xxRedirection()) 
        .andExpect(view().name("redirect:/"))
        .andExpect(authenticated());
    Assert.assertTrue(memberRepository.existsByEmail("a@a.a"));

    then(emailService).should().sendEmail(any(EmailMessage.class)); // 이 부분!

}
```



## application.yml 

```yml
  profiles:
    active: dev # local 과 dev 번갈아 테스트
```









****

# Thymeleaf 로 이메일 html 보내기



## templates/mail/simple-link.html 추가

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8">
  <title>Title</title>
</head>
<body>
  <div>
    <h2>안녕하세요.</h2>
    <p th:text="${message}"></p>
    <a th:href="${host+link}" th:text="${linkName}">Link</a>
    <p>링크가 동작하지 않는 경우에는 아래 URL을 웹브라우저에 복사해서 붙여 넣으세요.</p>
    <small th:text="${host+link}"></small>
  </div>
  <footer>
    <small>My Book Store&copy;2021</small>
  </footer>
</body>
</html>
```



## MemberService

- 빈 추가

```java
private final TemplateEngine templateEngine;
```



## application.yml

```yaml
app.host: http://127.0.0.1:8080  # 운영환경에서는 도메인 주소로 변경!
```



## build.gradle

사용자 정의 설정을 사용할 경우(여기선 `app.host`) 어노테이션 프로세서가 필요함.

```groovy
annotationProcessor "org.springframework.boot:spring-boot-configuration-processor"
```



## AppProperties 클래스 추가

```java
package com.megait.myhome.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties("app")
public class AppProperties {
    private String host;
}

```



## MemberService

- `sendSignupConfirmEmail()` 수정

```java
public void sendSignupConfirmEmail(Member newMember) {
    sendEmail(newMember, "My Book Store - 회원 가입 인증");
}
```

- `sendMailResetPassword()` 수정

```java
public void sendMailResetPassword(String email) {
    Member member = memberRepository.findByEmail(email);
    if(member == null) {
        return;
    }
    sendEmail(member, "My Book Store - 비밀번호 재설정 링크입니다.");
}
```

- `sendEmail()` 추가

```java
private void sendEmail(Member member, String subject) {
    Context context = new Context();
    context.setVariable("link", "/check-email-token?token="+ member.getEmailCheckToken() + "&email=" + member.getEmail());
    context.setVariable("host", appProperties.getHost());
    context.setVariable("linkName", "이메일 인증하기");
    context.setVariable("message", "서비스 이용을 위해 링크를 클릭하세요.");
    String html = templateEngine.process("mail/simple-link", context);
    EmailMessage emailMessage = EmailMessage.builder()
        .to(member.getEmail())
        .subject(subject)
        .message(html)
        .build();
    emailService.sendEmail(emailMessage);
}
```

