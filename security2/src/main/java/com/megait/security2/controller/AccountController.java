package com.megait.security2.controller;

import com.megait.security2.Entity.Account;
import com.megait.security2.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;

    // for test
    @GetMapping("/account/{role}/{username}/{password}")
    public Account account(@ModelAttribute Account account){
        log.info(account.getUsername() + " " + account.getPassword() + " " + account.getRole());
        return accountService.createNewAccount(account);
    }
}
