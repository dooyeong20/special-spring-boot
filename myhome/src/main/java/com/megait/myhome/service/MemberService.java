package com.megait.myhome.service;

import com.megait.myhome.config.AppProperties;
import com.megait.myhome.domain.Address;
import com.megait.myhome.domain.Member;
import com.megait.myhome.domain.MemberType;
import com.megait.myhome.form.SignupForm;
import com.megait.myhome.form.SignupFormValidator;
import com.megait.myhome.repository.MemberRepository;
import com.megait.myhome.util.EmailMessage;
import com.megait.myhome.util.HtmlEmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final SignupFormValidator signupFormValidator;
    private final TemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;
    private final HtmlEmailService htmlEmailService;
    private final AppProperties appProperties;

    @PostConstruct
    public void createTestUser(){
        String email = "test@test.com";
        String password = "1234";

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        member.generateEmailCheckToken();
        memberRepository.save(member);
    }


    @InitBinder("signupForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signupFormValidator);
    }

    public Member saveNewMember(SignupForm signupForm) {
        Member member = Member.builder()
                .email(signupForm.getEmail())
                .password(passwordEncoder.encode(signupForm.getPassword()))
                .address(Address.builder()
                        .city(signupForm.getCity())
                        .street(signupForm.getStreet())
                        .zip(signupForm.getZipcode())
                        .build())
                .build();

        return memberRepository.save(member);
    }

    public void sendSignupConfirmEmail(Member newMember) {
        String url = "https://localhost:8080/check-email-token";
        sendEmail(newMember, "My Book Store - 회원 가임 인증",  url);
    }

    public void login(Member member) {
        MemberUser memberUser = new MemberUser(member);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        memberUser, //member.getEmail(), // Principal
                        memberUser.getMember().getPassword(), //member.getPassword(),
                        memberUser.getAuthorities() //List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }

    @Transactional
    public Member processNewMember(SignupForm signupForm) {
        Member newMember = saveNewMember(signupForm);

        newMember.generateEmailCheckToken();
        newMember.setType(MemberType.USER);

        sendSignupConfirmEmail(newMember);
        return newMember;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username);
        if(member == null){
            throw new UsernameNotFoundException(username);
        }

        return new MemberUser(member);
    }

    public boolean sendResetPasswordEmail(String email) {

        Member member = memberRepository.findByEmail(email);
        if(member == null){
            return false;
        }

        String url = "http://localhost:8080/reset-password";
        sendEmail(member, "My Book Store - 비밀번호 인증",  url);

        return true;
    }

    public void resetPassword(Member member, String newPassword) {
        member.setPassword(passwordEncoder.encode(newPassword));
    }

    public Member getMemberById(Long id) {
        Optional<Member> member = memberRepository.findById(id);

        return member.isEmpty() ? null : member.get();
    }

    private void sendEmail(Member member, String subject, String url){
        Context context = new Context();
        context.setVariable("link", url
                + "?token=" + member.getEmailCheckToken()
                + "&email=" + member.getEmail()
        );
        context.setVariable("host", appProperties.getHost());
        context.setVariable("linkName", "verify email");
        context.setVariable("message", "click link for your service");

        String html = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(member.getEmail())
                .subject(subject)
                .message(html)
                .build();

        htmlEmailService.sendEmail(emailMessage);
    }
}