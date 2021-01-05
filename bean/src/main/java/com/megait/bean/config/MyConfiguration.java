package com.megait.bean.config;

import com.megait.bean.service.CartService;
import com.megait.bean.service.CartServiceImpl;
import com.megait.bean.service.UserService;
import com.megait.bean.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiguration {

    @Bean   // MyConfiguration 파일이 '설정 파일'로 등록된 경우, 이 빈을 생성한다.
            // applicationContext 객체에 Singleton으로 저장된다.
    public CartService cartService(){
        CartServiceImpl service = new CartServiceImpl();
        service.setName("장바구니 서비스");
        return service;
    }

    @Bean
    public UserService userService(){
        UserServiceImpl service = new UserServiceImpl();
        service.setName("회원 서비스");
        return service;
    }

}
