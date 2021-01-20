package com.megait.myhome.form;

import com.megait.myhome.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class SignupFormValidator implements Validator {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignupForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupForm signupForm = (SignupForm)target;
        if(memberRepository.existsByEmail(signupForm.getEmail())){
            errors.rejectValue("email", "invalid.email", new Object[]{signupForm.getEmail()}, "이미 사용중인 이메일입니다.");
        }

    }
}
