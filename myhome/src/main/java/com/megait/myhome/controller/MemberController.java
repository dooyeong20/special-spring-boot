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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class MemberController {
    private final Logger logger = LoggerFactory.getLogger(getClass());


    private MemberService memberService;

    // tmp
    private MemberRepository memberRepository;

    @Autowired
    public MemberController(MemberService memberService, MemberRepository memberRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

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
        model.addAttribute(new Member());

        return "view/user/checked-email";
    }

    @GetMapping("/change-password")
    public String changePassword(){
        return "view/user/change-password";
    }

    @PostMapping("/send-reset-mail")
    public String sendResetPassword(@Param("email") String email){
        if(!memberService.sendResetPasswordEmail(email)){
            logger.info("member not found ! (by email)");
        }

        return "view/index";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(String token, String email, Model model){
        Member member = memberRepository.findByEmail(email);
        if(member == null || !member.getEmailCheckToken().equals(token)){
            model.addAttribute("error", "reset password error");
            return "view/user/reset-password-result";
        }

        model.addAttribute("email", member.getEmail());
        model.addAttribute("member", new Member());

        return "view/user/reset-password-result";
    }

    @PostMapping("/reset-password")
    public String resetPassword(Member member, String email, Model model){
        String newPassword = member.getPassword();
        member = memberRepository.findByEmail(email);
        memberService.resetPassword(member, newPassword);

        model.addAttribute("result_code", "password.reset.complete");
        model.addAttribute("reset_message", "패스워드 변경 완료 !");

        memberService.login(member); // 바꾼 후 자동 로그인

        return "view/index";
    }

}
