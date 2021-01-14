package com.megait.myhome.service;

import com.megait.myhome.domain.User;
import com.megait.myhome.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    Long signUp(String username, String password);
}
