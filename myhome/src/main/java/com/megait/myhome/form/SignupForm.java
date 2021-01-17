package com.megait.myhome.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignupForm {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String  password;

    @NotBlank
    private String agreeTermsOfService;

    private String city;

//    @Pattern(regexp = "^0-9{5}$")
    private String zipcode;

    private String street;
}