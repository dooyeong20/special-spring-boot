package com.megait.security2.controller;

import com.megait.security2.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class MyController {
    // "/" ==> 인증됨 : Hello, ~~님!
    //      ==> 익명 : Hello, security !
    @Autowired
    SampleService sampleService;

    @GetMapping("/")
    public String index(Model model, Principal principal){
        if(principal == null){
            model.addAttribute("message", "Hello, Spring Security !");
        }
        else{
            model.addAttribute("message", "Hello, " + principal.getName());
        }

        return "index";
    }

    @GetMapping("/info")
    public String info(Model model, Principal principal){
        model.addAttribute("message", "Hello, " + principal.getName());

        return "info";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal){
        model.addAttribute("message", "Hello, " + principal.getName());
        sampleService.dashboard();
        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin(Model model, Principal principal){
        model.addAttribute("message", "Hello, Administrator !");

        return "admin";
    }
}
