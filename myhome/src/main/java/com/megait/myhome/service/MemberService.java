package com.megait.myhome.service;

import com.megait.myhome.domain.Address;
import com.megait.myhome.domain.Member;
import com.megait.myhome.domain.MemberType;
import com.megait.myhome.form.SignupForm;
import com.megait.myhome.form.SignupFormValidator;
import com.megait.myhome.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

@Service
public class MemberService implements UserDetailsService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired

    private MemberRepository memberRepository;

    @Autowired
    private SignupFormValidator signupFormValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(newMember.getEmail());
        simpleMailMessage.setSubject("My Book Store 회원 가입 인증");
        simpleMailMessage.setText("/check-email-token?token="+ newMember.getEmailCheckToken() + "&email=" + newMember.getEmail());

        javaMailSender.send(simpleMailMessage);
    }

    public void login(Member member) {
        // Username, password 정보를 가지고
        // 인증 요청을 보냄. 발생한 인증 토큰을 가지고
        // SecurityContext 에 인증 토큰 저장 (로그인된 유저 추가)

        MemberUser memberUser = new MemberUser(member);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        memberUser, //member.getEmail(), // Principal
                        memberUser.getMember().getPassword(), //member.getPassword(),
                        memberUser.getAuthorities() //List.of(new SimpleGrantedAuthority("ROLE_USER")) // TODO 사용자 권한 관련
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
}
