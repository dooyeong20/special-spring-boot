package com.megait.myhome.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SignupForm {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private String street;
    private String city;
    private String zipcode;

    @NotBlank
    private String agreeTermsOfService;
}
