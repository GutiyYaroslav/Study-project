package com.example.studyproject.repositories;

import com.example.studyproject.models.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test

    void userRepository_existUserByEmail_UserExists(){
        User user = User.builder()
                .firstName("Slavko")
                .lastName("Gutiy")
                .email("slavko@gmail.com")
                .password("Slavko1996$")
                .build();
        userRepository.save(user);

        boolean result = userRepository.existsUserByEmail("slavko@gmail.com");

        Assertions.assertThat(result).isTrue();
    }

    @Test

    void userRepository_existUserByEmail_UserDoesNotExist(){

        boolean result = userRepository.existsUserByEmail("slavko@gmail.com");

        Assertions.assertThat(result).isFalse();
    }
}
