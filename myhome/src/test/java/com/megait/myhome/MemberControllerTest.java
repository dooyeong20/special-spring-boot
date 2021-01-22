package com.megait.myhome;

import com.megait.myhome.domain.Member;
import com.megait.myhome.repository.MemberRepository;
import com.megait.myhome.util.ConsoleMailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {

    private final MockMvc mockMvc;
    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public MemberControllerTest(MockMvc mockMvc, MemberRepository memberRepository, PasswordEncoder encoder) {
        this.mockMvc = mockMvc;
        this.memberRepository = memberRepository;
        this.encoder = encoder;
    }

    @MockBean
    private ConsoleMailSender mailSender;

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
                        .param("email", "a@a.com")
                        .param("password", "aaaa")

                        // 약관 동의 체크 안하고...

                        .with(csrf()))  // Post 방식의 <form> 전송인 경우는 무조건 쓰자!
                                        // (타임리프 폼인 경우에는 csrf 토큰이 hidden 파라미터로 있다,)
                .andExpect(status().isOk())
                .andExpect(view().name("view/user/signup"));

    }

    @DisplayName("회원 가입 - 입력값 정상")
    @Test
    void signupSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "a@a.a")
                        .param("password", "1234")
                        .param("agreeTermsOfService", "true")  // 약관 동의 체크도 잘함
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())  // 가입 성공하면 '/'로 리다이렉트 되도록 구현했었다
                .andExpect(view().name("redirect:/"))
                .andExpect(redirectedUrl("/"));

        // 실제로 디비에도 a@a.a 가 들어갔을까?
//        Assert.assertTrue(memberRepository.existsByEmail("a@a.a"));

        // mailSender 가 send(SimpleMailMessage message)를 호출했는지 확인.
        then(mailSender).should().send(any(SimpleMailMessage.class));

        // then(대상).should().대상_메서드()
        // any(SimpleMailMessage.class) : SimpleMailMessage.class 타입의 `아무` 객체

        // 'given - when - then' 패턴
    }

    @DisplayName("인증 메일 확인 - 입력값 오류")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception {
        mockMvc.perform(get("/check-email-token")
                    .param("token", "qweqwe")
                    .param("email", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("view/user/checked-email"))
                .andExpect(unauthenticated());
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
                .andExpect(view().name("view/user/checked-email"))
                .andExpect(authenticated());
    }

    @DisplayName("로그인 확인 - 비밀번호 틀림")
    @Test
    @Rollback(value = false)
    void login_with_wrong_password() throws Exception{
        // Given 새 회원 추가
        // 이메일, 비밀번호(인코딩) 넣기
        // memberRepository 에 저장

        Member member = getMember("abc@test.com", "pw");
        member.generateEmailCheckToken();
        memberRepository.save(member);

        // When
        // 'login' 요청을 날림
        // 'post' 방식
        // username, password 파라미터 넣기
        mockMvc.perform(post("/login")
                .param("username", "abc@test.com")
                .param("password", "wrong password")
                .with(csrf()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());

        // then
        // Assert
    }

    @DisplayName("로그인 확인 - 비밀번호 정확")
    @Test
    @Rollback(value = false)
    void login_with_correct_password() throws Exception{
        Member member = getMember("a@a.a", "pw");
        member.generateEmailCheckToken();
        memberRepository.save(member);

        mockMvc.perform(post("/login")
                .param("username", "a@a.a")
                .param("password", "pw")
                .with(csrf()))

                .andExpect(authenticated())
                .andExpect(redirectedUrl("/"));
    }

    private Member getMember(String email, String pw) {
        return Member.builder()
                .email(email)
                .password(encoder.encode(pw))
                .build();
    }

    @DisplayName("패스워드 재설정 확인")
    @Test
    @Rollback(value = false)
    void reset_password() throws Exception{
        String email = "a@a.a";
        String pw = "test";

        Member member = getMember(email, pw);
        member.generateEmailCheckToken();
        memberRepository.save(member);

        mockMvc.perform(post("/send-reset-mail")
                .param("email", email)
                .with(csrf()))

                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(view().name("view/index"));
    }
}
