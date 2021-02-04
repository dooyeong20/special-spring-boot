package com.megait.security2.config;

import com.megait.security2.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import java.util.Arrays;
import java.util.List;

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
                    .mvcMatchers("/dashboard").hasRole("USER")
                    // 그 외 요청은 모두 인증(로그인)을 해야만 가능
                    .anyRequest().authenticated()

                    .accessDecisionManager(getMyAccessDecisionManager())
                .and()
                    .formLogin()
                    .permitAll()
                .and()
                    .httpBasic();
    }

    private AccessDecisionManager getMyAccessDecisionManager() {
        // 권한의 계층 구조
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER > ROLE_GUEST");

        // 검사의 기준 (계층 구조에 따라서)
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);

        // voter
        WebExpressionVoter voter = new WebExpressionVoter();
        voter.setExpressionHandler(handler);

        List<AccessDecisionVoter<?>> voters = Arrays.asList(voter);

        return new AffirmativeBased(voters);
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