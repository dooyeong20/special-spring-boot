package com.megait.myhome.controller;

import com.megait.myhome.domain.Book;
import com.megait.myhome.domain.Item;
import com.megait.myhome.domain.Member;
import com.megait.myhome.service.CurrentUser;
import com.megait.myhome.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/")
    public String home(@CurrentUser Member member, Model model, @RequestParam(required = false) String type){

        if(member != null){
            model.addAttribute(member);
        }

        if(type != null && List.of("B", "G", "A").contains(type)) {
            List<Item> itemList = itemService.findAllByDtype(type);
            model.addAttribute("itemList", itemList);
        }


        return "/view/index";
    }

    @GetMapping("/login")
    public String login(){

        return "/view/user/login";
    }

}
