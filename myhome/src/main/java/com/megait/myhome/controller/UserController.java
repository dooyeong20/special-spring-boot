package com.megait.myhome.controller;

import com.megait.myhome.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String mainPage(){
        return "index";
    }

    @RequestMapping("/login")
    public String showLoginPage(HttpServletRequest req){
        Cookie[] cookies = req.getCookies();
        String rememeredId = "";

        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("remember_id")){
                    rememeredId = cookie.getValue();
                    break;
                }
            }
        }

        req.setAttribute("remembered_id", rememeredId);

        return "/view/user/login";
    }

    @RequestMapping("/login.do")
    public String loginDo(@RequestParam("user_id") String id,
                          @RequestParam("user_pw") String pw,
                          @RequestParam(value = "remember", required = false) Object rem,
                          HttpServletRequest req, HttpServletResponse resp){

        boolean remember = false;
        boolean result = false;

        if(rem != null){
            remember = true;
        }

        id = id.trim();
        pw = pw.trim();

        HttpSession session = req.getSession();

        // if loginn success
        if(id.equals("admin") && pw.equals("admin1234")){
            if(remember){
                Cookie cookie = new Cookie("remember_id", id);
                cookie.setMaxAge(60 * 60 * 5);  // 5 hours
                resp.addCookie(cookie);
            }

            result = true;
            session.setAttribute("userId", id);
            session.setMaxInactiveInterval(60);
        }

        return "index";
    }

    @RequestMapping("/logout.do")
    public String   logout(HttpServletRequest req){
        req.getSession().invalidate();

        return "index";
    }
}
