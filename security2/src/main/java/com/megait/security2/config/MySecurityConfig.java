package com.megait.security2.config;

import com.megait.security2.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AccountService accountService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // "/", "/info", ... 등은 모두 요청 가능
                    .mvcMatchers("/", "/info", "/account/**").permitAll()
                    // "/admin"은 "ADMIN" 권한(Role)을 가진 사용자만 요청 가능
                    .mvcMatchers("/admin").hasRole("ADMIN")
                    // 그 외 요청은 모두 인증(로그인)을 해야만 가능
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .permitAll()
                .and()
                    .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("test01").password(passwordEncoder().encode("1234")).roles("USER").and()
//                .withUser("test02").password(passwordEncoder().encode("1234")).roles("USER").and()
//                .withUser("test03").password(passwordEncoder().encode("1234")).roles("ADMIN");

        auth.userDetailsService(accountService);
    }



    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}