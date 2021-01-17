package com.megait.myhome.controller;

import com.megait.myhome.domain.Address;
import com.megait.myhome.domain.Member;
import com.megait.myhome.form.SignupForm;
import com.megait.myhome.repository.MemberRepository;
import com.megait.myhome.util.ConsoleMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
public class MemberController {

    private MemberRepository memberRepository;
    private Logger logger;
    private ConsoleMailSender consoleMailSender;

    @Autowired
    public MemberController(MemberRepository memberRepository, ConsoleMailSender consoleMailSender) {
        this.memberRepository = memberRepository;
        this.logger = LoggerFactory.getLogger(getClass());
        this.consoleMailSender = consoleMailSender;
    }


    @RequestMapping("/")
    public String mainPage(){
        return "view/index";
    }

    @GetMapping("/signup")
    public String signupForm(Model model){
        model.addAttribute(new SignupForm());

        return "view/user/signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@Valid /* annotation으로 설정한거 + validate 수행*/ @ModelAttribute SignupForm signupForm,
                               Errors errors /* 에러가 났을 때 errors에 담김*/){

        if(errors.hasErrors()){
            logger.warn("검증 실패 ...");
            return "view/user/signup";
        }
        logger.info("검증 성공 !!");

        // DB에 저장
        Member member = Member.builder()
                .email(signupForm.getEmail())
                .password(signupForm.getPassword())
                .address(Address.builder()
                        .zip(signupForm.getZipcode())
                        .city(signupForm.getCity())
                        .street(signupForm.getStreet())
                        .build())
                .build();
        member.generateEmailCheckToken();
        Member savedMember = memberRepository.save(member);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(savedMember.getEmail());

        mailMessage.setSubject("회원가입 인증 메일입니다.");
        mailMessage.setText("/check-email-token?token=" + savedMember.getEmailCheckToken()
                            + "&email=" + savedMember.getEmail());

        consoleMailSender.send(mailMessage);

        // 이메일 검증 링크 보내기 (인증)
        // TODO 진짜 이메일 보내기


        // return "/" <-- 여기로 리다이렉트
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "view/user/login";
    }
}
