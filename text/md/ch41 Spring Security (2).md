

# 인증(Authentication)과 인가(Authorization)

- 인증 (Authentication)
  - 사용자가 본인이 맞는지 확인하는 절차

- 인가 (Authorization)
  - 인증된 사용자가 요청된 자원에 접근할 권한이 있는지 확인하는 절차

****



# SecurityContextHolder

- `SecurityContext` 를 제공.

- 기본적으로 ThreadLocal을 사용한다.

  

## SecurityContext 란?

- `Authentication` 객체들을 담고 있다.



## Authentication이란?

- `Principal`과 `GrantAuthority`를 제공한다.



### Principal 

- "누구"
- `UserDetailsService`의 `loadByUsername()`에서 리턴하는 `UserDetails` 객체에 들어있다.



### GrantAuthority

- "ROLE_USER", "ROLE_ADMIN" 등 Principal이 가지고 있는 "권한"
- "ROLE_" prefix를 사용한다.
- 인가 및 권한 확인에 GrantAuthority 정보를 참조한다.



### UserDetails

- 애플리케이션이 가지고 있는 유저 정보와 스프링 시큐리티의 Authentication 사이의 어댑터



### UserDetailsService

- 유저 정보를 UserDetails 타입으로 가져오는 DAO 인터페이스
- 이것을 우리가 구현한다.



# Spring Security 의 인증 과정

1. username과 password를 조합해서 `UsernamePasswordAuthenticationToken` 인스턴스 생성
2. 생성된 `UsernamePasswordAuthenticationToken`은 검증을 위해 `AuthenticationManager`로 전달
3. `AuthenticationManager`는 해당 토큰이 올바른 인증인지 판단되면 `Authentication` 인스턴스를 생성하여 이를 리턴.
4. `Authentication`을 `SecurityContext`에 저장



# SampleController

```java
@Autowired
SampleService sampleService;

// 로그인을 한 사용자만 접근 가능
@GetMapping("/dashboard")
public String dashboard(Model model, Principal principal) {
    model.addAttribute("message", "Hello, " + principal.getName());
    sampleService.dashboard(); // TODO 1
    return "dashboard";
}
```



# SampleService	

```java
package com.megait.form;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

// TODO 0
@Service
public class SampleService {

    // TODO 2
    public void dashboard() {
        
        // 확인 방법 1
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Object credentials = authentication.getCredentials();
        boolean authenticated = authentication.isAuthenticated();
        
        // 확인 방법 2
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        log.info("user : " + userDetails.getUsername());
    }
}
```



