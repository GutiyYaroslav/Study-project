package com.example.studyproject.utils.converter;

import com.example.studyproject.dto.UserDTO;
import com.example.studyproject.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserDTOToUserConverter {
    public User convertUserDTOToUser(UserDTO userDTO){
        return User.builder()
                .id(userDTO.getId())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .build();
    }
}
