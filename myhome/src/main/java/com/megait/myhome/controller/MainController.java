package com.megait.myhome.controller;

import com.megait.myhome.domain.Album;
import com.megait.myhome.domain.Book;
import com.megait.myhome.domain.Item;
import com.megait.myhome.domain.Member;
import com.megait.myhome.service.CurrentUser;
import com.megait.myhome.service.ItemService;
import com.megait.myhome.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;
    private final MemberService memberService;
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @GetMapping("/")
    public String home(@CurrentUser Member member, Model model){

        if(member != null){
            model.addAttribute(member);
        }

        List<Album> albumList =itemService.getAlbums();
        List<Book> bookList = itemService.getBooks();

        model.addAttribute("albumList", albumList);
        model.addAttribute("bookList", bookList);

        return "/view/index";
    }

    @GetMapping("/login")
    public String login(){
        return "/view/user/login";
    }


    @GetMapping("/store/detail")
    public String detail(Long id, Model model){
        Item item = itemService.getItem(id);
        model.addAttribute("item", item);

        return "view/store/detail";
    }

    @PostMapping("/store/like")
    public String add_like(@CurrentUser Member member, Long id, String test){
        logger.info("----> liked !");

        Item item = itemService.getItem(id);
        Member currentMember = memberService.getMemberById(member.getId());
        itemService.addLike(currentMember, item);

        return "view/index";
    }

}
