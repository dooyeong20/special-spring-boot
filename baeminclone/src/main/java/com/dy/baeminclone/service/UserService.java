package com.dy.baeminclone.service;

import com.dy.baeminclone.domain.Cart;
import com.dy.baeminclone.domain.User;
import com.dy.baeminclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;


    public User signUp(User user) {
        user.setRegdate(LocalDateTime.now());
        try {
            Cart cart = new Cart(user);
            userRepository.save(user);
        } catch (Throwable e){
            logger.error(e.getMessage());
            return null;
        }

        return user;
    }

    public boolean signIn(User user) {
        return userRepository.existsByUser(user);
    }
}
