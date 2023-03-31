package com.example.studyproject.services;

import com.example.studyproject.dto.UserDTO;
import com.example.studyproject.exceptions.UserAlreadyExistsException;
import com.example.studyproject.exceptions.UserNotFoundException;
import com.example.studyproject.models.User;

public interface UserService {
    User create(UserDTO userDTO) throws UserAlreadyExistsException;
    User getById(Long userId) throws UserNotFoundException;
    void deleteById(Long userId) throws UserNotFoundException;
    User edit(UserDTO userDTO) throws UserAlreadyExistsException, UserNotFoundException;
}
