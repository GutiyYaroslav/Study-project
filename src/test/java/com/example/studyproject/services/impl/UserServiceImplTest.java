package com.example.studyproject.services.impl;

import com.example.studyproject.dto.UserDTO;
import com.example.studyproject.exceptions.UserAlreadyExistsException;
import com.example.studyproject.exceptions.UserNotFoundException;
import com.example.studyproject.models.User;
import com.example.studyproject.repositories.UserRepository;
import com.example.studyproject.utils.converter.UserDTOToUserConverter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_whenUserDoesNotExist_successCreate() throws UserAlreadyExistsException {
        UserDTO userDTO = UserDTO.builder()
                .firstName("Slavko")
                .lastName("Gutiy")
                .email("slavko@gmail.com")
                .password("Slavko1234$")
                .build();
        User user = UserDTOToUserConverter.convertUserDTOToUser(userDTO);

        when(userRepository.existsUserByEmail("slavko@gmail.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.create(userDTO);

        Assertions.assertThat(createdUser).isNotNull();
        Assertions.assertThat(createdUser.getFirstName()).isEqualTo(user.getFirstName());
        Assertions.assertThat(createdUser.getLastName()).isEqualTo(user.getLastName());
        Assertions.assertThat(createdUser.getEmail()).isEqualTo(user.getEmail());

        verify(userRepository, Mockito.times(1)).existsUserByEmail(userDTO.getEmail());
        verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    void create_whenUserExists_throwException() throws UserAlreadyExistsException {
        UserDTO userDTO = UserDTO.builder()
                .firstName("Slavko")
                .lastName("Gutiy")
                .email("slavko@gmail.com")
                .password("Slavko1234$")
                .build();
        when(userRepository.existsUserByEmail(userDTO.getEmail())).thenReturn(true);


        Assertions.assertThatThrownBy(() -> userService.create(userDTO))
                        .isInstanceOf(UserAlreadyExistsException.class)
                        .hasMessageContaining("User with email " + userDTO.getEmail() + " already exists");


        verify(userRepository, Mockito.times(1)).existsUserByEmail(userDTO.getEmail());
        verify(userRepository, Mockito.times(0)).save(any(User.class));
    }

    @Test
    void getById_whenUserFound_returnUser(){
        User user = User.builder()
                .firstName("Slavko")
                .lastName("Gutiy")
                .email("slavko@gmail.com")
                .password("Slavko1234$")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User currentUser = userService.getById(1L);
        Assertions.assertThat(currentUser).isNotNull();
        Assertions.assertThat(currentUser.getFirstName()).isEqualTo(user.getFirstName());
        Assertions.assertThat(currentUser.getLastName()).isEqualTo(user.getLastName());
        Assertions.assertThat(currentUser.getEmail()).isEqualTo(user.getEmail());

        verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void getById_whenUserNotFound_throwException(){

        when(userRepository.findById(0L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.getById(0L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with id 0 not found");
        verify(userRepository, Mockito.times(1)).findById(0L);
    }

    @Test
    void deleteById_whenUserExists_deleteUser(){
        User user = User.builder()
                .id(1L)
                .firstName("Slavko")
                .lastName("Gutiy")
                .email("slavko@gmail.com")
                .password("Slavko1234$")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteById(1L);

        verify(userRepository, Mockito.times(1)).delete(user);
        verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void deleteById_whenUserDoesNotExist_throwException(){

        when(userRepository.findById(0L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.deleteById(0L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with id 0 not found");

        verify(userRepository, Mockito.times(1)).findById(0L);
    }

    @Test
    void edit_whenUserDoesNotFound_throwException(){
        UserDTO userDTO = UserDTO.builder()
                .id(0L)
                .firstName("Slavko")
                .lastName("Gutiy")
                .email("slavko@gmail.com")
                .password("Slavko1234$")
                .build();

        when(userRepository.findById(0L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.edit(userDTO))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with id 0 not found");

        verify(userRepository, Mockito.times(1)).findById(0L);
    }

    @Test
    void edit_whenUserExists_throwException(){
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .firstName("Slavko")
                .lastName("Gutiy")
                .email("slavko@gmail.com")
                .password("Slavko1234$")
                .build();
        User user = User.builder()
                .id(2L)
                .firstName("Slavko")
                .lastName("Gutiy")
                .email("romko@gmail.com")
                .password("Slavko1234$")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsUserByEmail(userDTO.getEmail())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> userService.edit(userDTO))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("User with email slavko@gmail.com already exists");

        verify(userRepository).findById(1L);
        verify(userRepository).existsUserByEmail(userDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void edit_whenUserFound_editUser(){
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .firstName("Slavko")
                .lastName("Gutiy")
                .email("slavko@gmail.com")
                .password("Slavko1234$")
                .build();
        User existingUser = User.builder()
                .id(1L)
                .firstName("Slavik")
                .lastName("Gutiy")
                .email("romko@gmail.com")
                .password("Slavko1234$")
                .build();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.existsUserByEmail(userDTO.getEmail())).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        User editedUser = userService.edit(userDTO);

        assertNotNull(editedUser);
        assertEquals(userDTO.getId(), editedUser.getId());
        assertEquals(userDTO.getFirstName(), editedUser.getFirstName());
        assertEquals(userDTO.getEmail(), editedUser.getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).existsUserByEmail(userDTO.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }
}