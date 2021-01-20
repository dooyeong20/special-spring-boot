package com.megait.myhome.service;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  // 어노테이션이 발현될 기간 : 실행까지 살아있음
@Target(ElementType.PARAMETER)      // 어노테이션을 붙일 수 있는 대상 : 매개변수에 붙일 수 있음
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : member")
public @interface CurrentUser {
}
