package com.megait.myhome.controller;

import com.megait.myhome.domain.Member;
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

import javax.transaction.Transactional;
import javax.validation.Valid;

@Controller
public class MemberController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MemberService memberService;

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

        // DB 에 저장
        Member newMember = memberService.saveNewMember(signupForm);

        // 이메일 검증 링크 보내주기 (인 척하기)
        // TODO 진짜 이메일 보내기
         // 이메일 토큰 생성 및 필드에 저장
        memberService.sendSignupConfirmEmail(newMember);

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
}
