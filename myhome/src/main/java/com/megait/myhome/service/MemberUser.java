package com.megait.myhome.service;

import com.megait.myhome.domain.Member;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;


/*
    spring security 에서는 유저 정보를 저장할 용도로 User 라는 클래스를 지원함.
 */

@Getter
public class MemberUser extends User {
    private Member member;

    public MemberUser(Member member){
        super(member.getEmail(), member.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        // List.of("abc", "def", "ghi") ===> ["abc", "def", "ghi"]
        this.member = member;
    }

}
