package com.example.studyproject.services.impl;

import com.example.studyproject.dto.UserDTO;
import com.example.studyproject.exceptions.UserAlreadyExistsException;
import com.example.studyproject.exceptions.UserNotFoundException;
import com.example.studyproject.models.User;
import com.example.studyproject.repositories.UserRepository;
import com.example.studyproject.services.UserService;
import com.example.studyproject.utils.converter.UserDTOToUserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDTOToUserConverter userConverter;

    @Override
    public User create(UserDTO userDTO) throws UserAlreadyExistsException {
        if(userRepository.existsUserByEmail(userDTO.getEmail())){
            throw new UserAlreadyExistsException("User with email " + userDTO.getEmail() + " already exists");
        }
        return userRepository.save(userConverter.convertUserDTOToUser(userDTO));
    }

    @Override
    public User getById(Long userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
    }

    @Override
    public void deleteById(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        userRepository.delete(user);
    }

    @Override
    public User edit(UserDTO userDTO) throws UserAlreadyExistsException, UserNotFoundException {
        User existingUser = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new UserNotFoundException("User with id " + userDTO.getId() + " not found"));
        if(!existingUser.getEmail().equals(userDTO.getEmail()) && userRepository.existsUserByEmail(userDTO.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + userDTO.getEmail() + " already exists");
        }
        return userRepository.save(userConverter.convertUserDTOToUser(userDTO));
    }
}
