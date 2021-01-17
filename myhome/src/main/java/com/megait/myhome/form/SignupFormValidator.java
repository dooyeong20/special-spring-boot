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
    public boolean supports(Class<?> clazz) {   // 이 validator가 검증할 수 있는 클래스
        return clazz.isAssignableFrom(SignupForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // target : SignupForm 객체
        // errors : SignupForm 에 선언된 Validation 조건을 확인하고, 실패하면 실패내역을 전달함

        SignupForm signupForm = (SignupForm) target;

//         추가 검증 실행 : 이메일 중복
        if(memberRepository.existsByEmail(signupForm.getEmail())){
            errors.rejectValue(
                    "email",
                    "invalid email",
                    new Object[]{signupForm.getEmail()},
                    "사용 중인 이메일 입니다.");
        }

//        만약 패스워드 일치를 검증한다면 ?
//        if(signupForm.getPassword().equals(signupForm.getPassword2())){
//            errors.rejectValue(
//                    "password",
//                    "wrong.password",
//                    null,
//                    "두 비밀번호가 일치하지 않습니다."
//            );
//        }

    }
}
