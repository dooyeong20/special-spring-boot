package com.megait.ch01.hello_spring_boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class HelloController {

    // @RequestMapping(value = "/", method = RequestMethod.GET)
    // @RequestMapping(value = "/") // Default = GET
    @RequestMapping("/")
    public String showGugudan(){
        return "index"; //  main/resource/templates/index.html
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/login.do")
    public ModelAndView loginDo(@RequestParam(value = "user_id") String id,
                                @RequestParam(value = "user_pw") String password,
                                HttpServletRequest req, HttpServletResponse resp){
        String[] idList = {"admin", "test01", "test02"};
        String[] pwList = {"admin1234", "1234", "5678"};
        String[] nickNameList = {"관리자", "유저1", "유저2"};

        boolean result = false;

        for(int i=0; i<idList.length; ++i){
            if((id.trim()).equals(idList[i]) && (password.trim()).equals(pwList[i])){
                result = true;
                req.getSession().setAttribute("nickName", nickNameList[i]);
                req.getSession().setMaxInactiveInterval(10);
                break;
            }
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("result", result);
        modelAndView.setViewName("login_result");

        return modelAndView;
    }

    @RequestMapping("/logout.do")
    public String logout(HttpServletRequest req){
        req.getSession().invalidate();
        return "index";
    }

    @RequestMapping("/season")
    public String showSeason(){
        return "season";
    }
}
