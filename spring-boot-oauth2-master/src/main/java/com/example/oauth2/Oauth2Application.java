package com.example.oauth2;

import com.example.oauth2.user.UserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class Oauth2Application extends WebMvcConfigurationSupport {

    private final UserArgumentResolver argumentResolver;

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(argumentResolver);
    }

    public static void main(String[] args) {
        SpringApplication.run(Oauth2Application.class, args);
    }
}