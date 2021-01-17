package com.megait.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/my")
    public String myPage(){
        return "my";
    }

    @RequestMapping("/hello")
    public String  helloPage(){
        return "hello";
    }
}
