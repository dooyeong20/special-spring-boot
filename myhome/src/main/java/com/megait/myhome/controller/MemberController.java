package com.megait.myhome.controller;

import com.megait.myhome.domain.Member;
import com.megait.myhome.form.SignupForm;
import com.megait.myhome.repository.MemberRepository;
import com.megait.myhome.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class MemberController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MemberService memberService;


    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute(/*"signupForm",*/ new SignupForm());
        return "view/user/signup";
    }

    @PostMapping("/signup")
    @Transactional
    public String signupSubmit(@Valid /*@ModelAttribute*/ SignupForm signupForm, Errors errors) {
        if (errors.hasErrors()) {
            logger.info("!!!!!!!!!!!!!!!!");
            return "view/user/signup";
        }

        Member newMember = memberService.processNewMember(signupForm);
        memberService.login(newMember);

        return "redirect:/";
    }
//
//    @GetMapping("/login")
//    public String loginForm(Model model) {
//        return "view/user/login";
//    }


    @Autowired
    MemberRepository memberRepository;

    @GetMapping("/check-email-token")
    @Transactional
    public String checkEmailToken(String token, String email, Model model){
        Member member = memberRepository.findByEmail(email);

        // 이메일이 없는 경우
        if(member == null){
            model.addAttribute("error", "wrong.email");
            return "view/user/checked-email";
        }

        // 잘못된 토큰 값인 경우
        if(!member.isValidToken(token)){
            model.addAttribute("error", "wrong.token");
            return "view/user/checked-email";
        }

        member.completeSignup();
        model.addAttribute("email", member.getEmail());
        return "view/user/checked-email";
    }


}
