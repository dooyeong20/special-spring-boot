package com.dy.baeminclone.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

//@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter
public class RequestUser {
    private String username;

    private String password;

    private String email;

    private String nickname;

    private String birth;

    private String tel;
}
