package com.example.studyproject.utils.converter;

import com.example.studyproject.dto.UserDTO;
import com.example.studyproject.models.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOToUserConverterTest {

    @Test
    void convertUserDTOToUser_shouldReturnUserByUserDTO() {
        UserDTO userDTO = UserDTO.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@domain.com")
                .password("Password#1")
                .build();
        User user = UserDTOToUserConverter.convertUserDTOToUser(userDTO);
        Assertions.assertThat(user.getFirstName()).isEqualTo(userDTO.getFirstName());
        Assertions.assertThat(user.getLastName()).isEqualTo(userDTO.getLastName());
        Assertions.assertThat(user.getEmail()).isEqualTo(userDTO.getEmail());
        Assertions.assertThat(user.getPassword()).isEqualTo(userDTO.getPassword());
        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user).isInstanceOf(User.class);
    }
}