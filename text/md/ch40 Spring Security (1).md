# 시작하기

## 의존성 추가하기



## pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    <dependency>
        <groupId>org.thymeleaf.extras</groupId>
        <artifactId>thymeleaf-extras-springsecurity5</artifactId>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
</dependencies>
```







## build.gradle

```groovy
implementation group: 'org.springframework.boot', name: 'spring-boot-starter-security'
```

**의존성만 추가하면 모든 페이지에 인증 필요** (로그인을 해야 한다.)



## Controller

```java
package com.megait.form;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class SampleController {

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("message", "Hello, Spring Security!");
        } else {
            model.addAttribute("message", "Hello, " + principal.getName());
        }
        return "index";
    }

    // 아무나 접근 가능
    @GetMapping("/info")
    public String info(Model model) {
        model.addAttribute("message", "Info");
        return "info";
    }

    // 로그인을 한 사용자만 접근 가능
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("message", "Hello, " + principal.getName());
        return "dashboard";
    }

    // 관리자만 접근 가능
    @GetMapping("/admin")
    public String admin(Model model, Principal principal) {
        model.addAttribute("message", "Hello, Admin " + principal.getName());
        return "admin";
    }

    // TODO html 페이지 4개 만들기

}
```



## 뷰(html) 만들기

info, dashboard, admin, index 페이지 생성



## config.SecurityConfig 만들기

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/info","/account/**").permitAll()
                .mvcMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .and()
            .httpBasic();
    }
}
```





****

# 인메모리 유저 등록



## application.properties

```properties
spring.security.user.name=admin
spring.security.user.password={noop}1234
spring.security.user.roles=ADMIN
```

이렇게 하면 `UserDetailsServiceAutoConfiguration`가 생성해준 기본 유저는 생성되지 않는다.

admin, 1234로 로그인 테스트를 해보자.





## 유저 여러명 등록하기

외부 설정을 통한 유저 등록은 최대 1명만 가능하다.

여러명을 추가하고 싶다면?

### SecurityConfig

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser("test01").password("{noop}1234").roles("USER").and()
        .withUser("test02").password("{noop}1234").roles("USER").and()
        .withUser("test03").password("{noop}1234").roles("USER").and()
        ;
}
```



# JPA 연동



## @SpringBootApplication

```java
package com.megait;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SecurityApplication {

	@Bean
	public PasswordEncoder passwordEncoder(){
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	public static void main(String[] args) {

		SpringApplication.run(SecurityApplication.class, args);
	}

}
```





## Account

```java
package com.megait.account;


import lombok.*;

import javax.persistence.*;


@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    public void encodePassword(PasswordEncoder passwordEncoder) {
        setPassword(passwordEncoder.encode(getPassword()));
    }
}
```



## Role

```java
package com.megait.account;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN, USER;

    @Override
    public String getAuthority() {
        return name();
    }
}

```



## AccountRepository

```java
package com.megait.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUsername(String username);
}

```



## AccountService

```java
package com.megait.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        return User.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .roles(account.getRole().getAuthority())
                .build();
    }
}
public Account createNewAccount(Account account) {
    account.encodePassword(passwordEncoder);
    return accountRepository.save(account);
}

```

**주의!** 사용자 정의 UserDetailsService  빈이 등록되면 인메모리 유저는 등록되지 않는다.



## AccountController

```java
package com.megait.account;
// TODO AccountController 만들기

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping("/account/{role}/{username}/{password}")
    public Account createAccount(@ModelAttribute Account account){
        return accountService.createNewAccount(account);
    }

}

```



## SecurityConfig 수정 

자동으로 등록되기 때문에 굳이 안해도 된다.

```java
package com.megait.config;

import com.megait.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountService accountService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/info", "/account/**").permitAll()
                .mvcMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .and()
            .httpBasic();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("test01").password("{noop}1234").roles("USER").and()
//                .withUser("test02").password("{noop}1234").roles("USER").and()
//                .withUser("test03").password("{noop}1234").roles("USER").and()
//                ;
        // 유저 정보에 관한 서비스를 등록한다.
        auth.userDetailsService(accountService);
    }
}

```



# Spring Security Test

## 의존성

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```



## 테스트 코드

```java
package com.megait;

import com.megait.account.Account;
import com.megait.account.AccountService;
import com.megait.account.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class SecurityApplicationTests {

	@Autowired
	AccountService accountService;

	@Autowired
	MockMvc mockMvc;

	@Test
	public void index_anonymous() throws Exception {
		mockMvc.perform(get("/").with(anonymous()))
				.andDo(print())
				.andExpect(status().isOk());
	}
	@Test
	public void index_user() throws Exception {
		mockMvc.perform(get("/").with(user("test").roles(Role.USER.name())))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@WithAnonymousUser
	public void admin_anonymous() throws Exception {
		mockMvc.perform(get("/admin"))
				.andDo(print())
				.andExpect(status().isUnauthorized());
	}
	@Test
	@WithMockUser
	public void admin_user() throws Exception {
		mockMvc.perform(get("/admin"))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void admin_admin() throws Exception {
		mockMvc.perform(get("/admin"))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void login_success() throws Exception{
		createTestUser();
		mockMvc.perform(formLogin().user("test01").password("1234"))
				.andExpect(authenticated());
	}

	@Test
	public void login_fail() throws Exception{
		createTestUser();
		mockMvc.perform(formLogin().user("test01").password("1234678"))
				.andExpect(unauthenticated());
	}

	private void createTestUser() {
		Account account = new Account();
		account.setUsername("test01");
		account.setPassword("1234");
		account.setRole(Role.USER);
		accountService.createNewAccount(account);
	}
}

```

