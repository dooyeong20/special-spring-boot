package com.example.template.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// @Controller("user")   --> localhost/user

@Controller
public class MyController {

    @RequestMapping("/")
    public String index(){
        return "page/index";
    }

    @RequestMapping("/page1")
    public String page1(){
        return "page/page1";
    }

    @RequestMapping("/page2")
    public String page2(){
        return "page/page2";
    }
}
