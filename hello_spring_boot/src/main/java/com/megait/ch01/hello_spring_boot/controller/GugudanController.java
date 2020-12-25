//package com.megait.ch01.hello_spring_boot.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.ModelAndView;
//
//@Controller
//public class GugudanController {
//
//    // @RequestMapping(value = "/", method = RequestMethod.GET)
//    // @RequestMapping(value = "/") // Default = GET
//    @RequestMapping("/")
//    public String showGugudan(){
//        return "index"; //  main/resource/templates/index.html
//    }
//
//    @RequestMapping("/login")
//    public String login(){
//        return "login";
//    }
//
//    @RequestMapping("/login.do")
//    public ModelAndView loginDo(@RequestParam(value = "user_id") String id,
//                                @RequestParam(value = "user_pw") String password){
//
//        boolean result = "admin".equals(id) && "admin1234".equals(password);
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("result",result);
//        modelAndView.setViewName("login_rsult");
//
//        return modelAndView;
//    }
//}
