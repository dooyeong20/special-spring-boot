package com.megait.myhome.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SignupForm {

    @NotBlank
    @Length(min = 5, max = 40)
    @Email
    private String email;

    @NotBlank
    private String password;


    @NotBlank
    private String agreeTermsOfService;

    private String street;
    private String city;
    private String zipcode;
}
