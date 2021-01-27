package com.megait.myhome.controller;

import com.google.gson.JsonObject;
import com.megait.myhome.domain.*;
import com.megait.myhome.service.CurrentUser;
import com.megait.myhome.service.ItemService;
import com.megait.myhome.service.MemberService;
import com.megait.myhome.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;
    private final MemberService memberService;
    private final OrderService orderService;
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

    @GetMapping(value="/store/like", produces = "application/text; charset=UTF-8")
    @ResponseBody
    public String addLike(@CurrentUser Member member, Long id) {
        JsonObject object = new JsonObject();

        if (member == null) {
            throw new IllegalStateException("로그인이 필요한 기능입니다.");
        }

        try {
            Member currentMember = memberService.getMemberById(member.getId());
            Item item = itemService.getItem(id);
            itemService.processLike(currentMember, item);
            object.addProperty("result", true);
            object.addProperty("message", "Success !");
        } catch (Exception e) {
            object.addProperty("result", false);
            object.addProperty("message", "장바구니에 담을 수 없습니다.");
        } finally {
            logger.info(object.toString());
        }

        return object.toString();
    }

    @GetMapping("/store/like-list")
    public String showList(@CurrentUser Member member, Model model){
        List<Item> likeList =
                memberService.getMemberById(member.getId()).getLikes();

        model.addAttribute("likeList", likeList);

        return "view/store/like-list";
    }

    @PostMapping("/cart/list")
        public String addCart(@CurrentUser Member member, @RequestParam("item_id") String[] itemId, Model model){
        List<Long> idList = List.of(Arrays.stream(itemId).map(Long::parseLong).toArray(Long[]::new));

        orderService.addCart(member, idList);
        itemService.deleteLikes(member, idList);

        return cartList(member, model);
    }

    @GetMapping("/cart/list")
    public String cartList(@CurrentUser Member member, Model model){
        try{
            List<OrderItem> cartList = orderService.getCart(member);
            model.addAttribute("cartList", cartList);
            model.addAttribute("totalPrice", orderService.getTotal(cartList));
        } catch (IllegalStateException e){
            model.addAttribute("error_message", "error");
        }

        return "view/cart/list";
    }
}
