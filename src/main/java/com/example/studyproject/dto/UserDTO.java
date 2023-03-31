package com.example.studyproject.dto;

import com.example.studyproject.validators.user.email.ValidEmail;
import com.example.studyproject.validators.user.name.ValidName;
import com.example.studyproject.validators.user.password.ValidPassword;
import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    @ValidName(message = "Invalid firstName field: The field firstName should not contain digits or symbols and must not be empty")
    private String firstName;
    @ValidName(message = "Invalid lastName field: The field lastName should not contain digits or symbols and must not be empty")
    private String lastName;
    @ValidEmail
    private String email;
    @ValidPassword
    private String password;
}
