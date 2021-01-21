package com.megait.myhome.controller;

import com.megait.myhome.domain.Member;
import com.megait.myhome.service.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentUser Member member, Model model){

        if(member != null){
            model.addAttribute(member);
        }

        return "view/index";
    }

    @GetMapping("/login")
    public String login(){

        return "/view/user/login";
    }

}
