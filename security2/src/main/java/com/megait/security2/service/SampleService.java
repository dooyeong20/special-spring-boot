package com.megait.security2.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SampleService {
    public void dashboard(){
        // Debug 모드로 디버깅하면 authentication 관련 확인 가능
        // 인증한 유저의 Authentication 받기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 그 안의 Principal 객체 받기
        Object principal = authentication.getPrincipal();

        // 권한 받기
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Credential 받기
        // Credential 은 인증이 끝나면 없어짐

        Object credentials = authentication.getCredentials();
        boolean authenticated = authentication.isAuthenticated();

    }
}