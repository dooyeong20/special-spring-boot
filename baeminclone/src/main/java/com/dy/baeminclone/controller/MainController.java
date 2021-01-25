package com.dy.baeminclone.controller;

import com.dy.baeminclone.domain.Cart;
import com.dy.baeminclone.domain.User;
import com.dy.baeminclone.rest.JsonResponse;
import com.dy.baeminclone.rest.RequestUser;
import com.dy.baeminclone.service.CartService;
import com.dy.baeminclone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PasswordEncoder encoder;
    private final UserService userService;

    private User getUser(RequestUser requestUser) {
        return User.builder()
                .username(requestUser.getUsername())
                .password(requestUser.getPassword())
                .email(requestUser.getEmail())
                .birth(requestUser.getBirth())
                .tel(requestUser.getTel())
                .nickname(requestUser.getNickname())
                .build();
    }

    @GetMapping("/encode-test")
    public String test(){
        return "test : " + encoder.encode("test");
    }

    @PostMapping("/users/signup")
    public JsonResponse signUp(@RequestBody RequestUser requestUser){
        JsonResponse response = new JsonResponse();

        User user = getUser(requestUser);
        try {
            response.setResult(userService.signUp(user) != null);
        } catch (UnexpectedRollbackException e) {
            logger.warn("Rollback");
        }

        return response;
    }

    @PostMapping("/users/signin")
    public JsonResponse signIn(@RequestBody RequestUser requestUser){
        JsonResponse response = new JsonResponse();
        User user = getUser(requestUser);
        boolean result;

        result = userService.signIn(user);

        if(result){
            response.getContent().put("username", user.getUsername());
            logger.info(user.toString());
        }

        response.setResult(result);

        return response;
    }



}
