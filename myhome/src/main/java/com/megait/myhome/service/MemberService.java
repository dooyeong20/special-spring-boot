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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

@Service
public class MemberService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SignupFormValidator signupFormValidator;

    @Autowired
    private PasswordEncoder encoder;

    @InitBinder("signupForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signupFormValidator);
    }

    public Member saveNewMember(SignupForm signupForm) {
        Member member = Member.builder()
                .email(signupForm.getEmail())
                .password(encoder.encode(signupForm.getPassword()))
                .address(Address.builder()
                        .zip(signupForm.getZipcode())
                        .city(signupForm.getCity())
                        .street(signupForm.getStreet())
                        .build())
                .build();

        return memberRepository.save(member);
    }

    public void sendSignupConfirmEmail(Member newMember) {
        newMember.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newMember.getEmail());
        mailMessage.setSubject("회원가입에 감사드립니다. 딱 한 과정이 남았습니다!");
        mailMessage.setText("/check-email-token?token="
                + newMember.getEmailCheckToken()
                + "&email=" + newMember.getEmail());
        javaMailSender.send(mailMessage);
    }


}
