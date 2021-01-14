package com.megait.myhome.service;

import com.megait.myhome.domain.User;
import com.megait.myhome.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long signUp(String username, String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        return userRepository.insert(user);
    }
}
