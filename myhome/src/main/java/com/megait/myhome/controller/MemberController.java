package com.megait.myhome.controller;

import com.megait.myhome.domain.Member;
import com.megait.myhome.form.SignupForm;
import com.megait.myhome.repository.MemberRepository;
import com.megait.myhome.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
public class MemberController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MemberService memberService;

    @Autowired
    MemberRepository memberRepository;


    @GetMapping("/signup")
    public String signupForm(Model model){
        model.addAttribute(new SignupForm());
        return "view/user/signup";
    }

    @PostMapping("/signup")
    @Transactional
    public String signupSubmit(@Valid SignupForm signupForm, Errors errors){
        if(errors.hasErrors()){
            logger.info("검증 실패.....");
            return "view/user/signup";
        }
        logger.info("검증 성공!!!");

        // process : save + send messgae
        Member newMember = memberService.processNewMember(signupForm);

        memberService.login(newMember);
        // return "/"; // <== 포워드
        return "redirect:/";  // <== 리다이렉트
    }

    @GetMapping("/login")
    public String loginForm(){
        return "view/user/login";
    }

    @RequestMapping
    public String index(){
        return "view/index";
    }


    @GetMapping("/check-email-token")
    @Transactional
    public String checkEmail(/* @Param("token") */ String token, /* @Param("email") */String email, Model model){
        Member member = memberRepository.findByEmail(email);

        if(member == null){
            model.addAttribute("error", "wrong.email");
            return "view/user/checked-email";
        }

        if(!member.hasValidToken(token)){
            model.addAttribute("error", "wrong.token");
            return "view/user/checked-email";
        }

        member.completeSignup();
        model.addAttribute("email", member.getEmail());

        return "view/user/checked-email";
    }

}