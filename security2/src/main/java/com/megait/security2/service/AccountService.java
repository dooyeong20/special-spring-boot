package com.megait.security2.service;

import com.megait.security2.Entity.Account;
import com.megait.security2.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);

        return User.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .roles(account.getRole().getAuthority())
                .build();
    }

    public Account createNewAccount(Account account) {
        account.encodePassword(passwordEncoder);
        return accountRepository.save(account);
    }
}