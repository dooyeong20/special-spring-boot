package com.megait.ch01.hello_spring_boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GugudanController {

    @GetMapping("/gugudan")
    public String showGugudan(){

        return "gugudan-result";
    }

}
