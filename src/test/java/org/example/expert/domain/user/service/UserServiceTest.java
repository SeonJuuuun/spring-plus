package org.example.expert.domain.user.service;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    public void saveMillionUsersParallel() {
        for (int t = 1; t <= 1000000; t++) {

            String nickname = "User" + t;

            userRepository.save(new User(nickname));
        }
    }
}
