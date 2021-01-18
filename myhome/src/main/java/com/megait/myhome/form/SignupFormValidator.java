package com.megait.myhome.form;

import com.megait.myhome.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

// Validator :
// SignupForm DTO에 선언된 검증 조건을 확인하고
// 검증 실패 시 어떤 처리를 할 것인가?

@Component
public class SignupFormValidator implements Validator {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) { // 이 Validator가 검증할 수 있는 클래스
        return clazz.isAssignableFrom(SignupForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // target : SignupForm 객체가 들어올 것임.
        // errors : SignupForm 에 선언된 Validation 조건을 확인하고 난 뒤
        //          검증 실패가 되면 그 실패 내역을 전달할 용도
        SignupForm signupForm = (SignupForm) target;

        // 추가 검증 실행 : 이메일 중복 확인 // TODO 에러 고치기
        if(memberRepository.existsByEmail(signupForm.getEmail())){
            errors.rejectValue(
                    "email",
                    "invalid.email",
                    new Object[]{signupForm.getEmail()},
                    "이미 사용 중인 이메일입니다."
            );
        }
    }
}
