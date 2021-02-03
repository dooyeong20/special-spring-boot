package com.megait.security2;

import com.megait.security2.Entity.Account;
import com.megait.security2.Entity.Role;
import com.megait.security2.service.AccountService;
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
public class AccountTest {

    private final MockMvc mockMvc;
    private final AccountService accountService;

    @Autowired
    public AccountTest(MockMvc mockMvc, AccountService accountService){
        this.mockMvc = mockMvc;
        this.accountService = accountService;
    }

    @Test
    void index_anonymous() throws Exception {
        mockMvc.perform(get("/").with(anonymous()))
                .andDo(print()) // print() : 응답된 메시지(html 포함)를 콘솔에 출력
                .andExpect(status().isOk());
    }

    @Test
    void index_user() throws Exception {
        mockMvc.perform(get("/").with(user("user01").roles(Role.USER.getAuthority())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void admin_anonymous() throws Exception {
        mockMvc.perform(get("/admin"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "test01", roles = "USER")
    void admin_user() throws Exception {
        mockMvc.perform(get("/admin"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void admin_admin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void login_success() throws Exception {
        createTestAccount();
        mockMvc.perform(formLogin("/login").user("test01").password("1234"))
                .andExpect(authenticated());
    }

    @Test
    void login_fail() throws Exception {
        createTestAccount();
        mockMvc.perform(formLogin("/login").user("test01").password("wrong password"))
                .andExpect(unauthenticated());

    }

    private void createTestAccount(){
        Account account = Account.builder()
                    .username("test01")
                    .password("1234")
                    .role(Role.USER)
                    .build();

        accountService.createNewAccount(account);
    }
}
