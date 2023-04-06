package com.example.studyproject.controllers;

import com.example.studyproject.dto.UserDTO;
import com.example.studyproject.models.User;
import com.example.studyproject.services.UserService;
import com.example.studyproject.utils.validation.ValidationError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")

public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void getById_ShouldReturnUser() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .firstName("Slavko")
                .lastName("Gutiy")
                .email("slavko@gmail.com")
                .password("Slavko1234$")
                .build();

        User user = userService.create(userDTO);
        String expectedResponse = objectMapper.writeValueAsString(user);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/users/" + user.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DirtiesContext
    public void getById_WhenUserNotFound_ShouldReturn404() throws Exception {
        ValidationError error = new ValidationError();
        error.setErrors(List.of("User with such id not found"));
        String expectedResponse = objectMapper.writeValueAsString(error);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/users/0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DirtiesContext
    public void add_ShouldReturnCreatedUser() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .firstName("Roman")
                .lastName("Chuhniy")
                .email("romko@gmail.com")
                .password("Romko1234$")
                .build();
        User user = User.builder()
                .id(1L)
                .firstName("Roman")
                .lastName("Chuhniy")
                .email("romko@gmail.com")
                .password("Romko1234$")
                .build();

        String requestBody = objectMapper.writeValueAsString(userDTO);
        String responseBody = objectMapper.writeValueAsString(user);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(actualResponse).isEqualTo(responseBody);
    }

    @Test
    @DirtiesContext
    public void add_WhenUserDTOInvalid_ShouldReturn400() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .lastName("Chuhniy")
                .email("romko@gmail.com")
                .password("Romko1234$")
                .build();

        ValidationError error = new ValidationError();
        error.setErrors(List.of("Invalid firstName field: The field firstName should not contain digits or symbols and must not be empty"));
        String expectedResponse = objectMapper.writeValueAsString(error);

        String requestBody = objectMapper.writeValueAsString(userDTO);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DirtiesContext
    public void add_WhenUserDTOsEmailAlreadyExist_ShouldReturn409() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .firstName("Olga")
                .lastName("Chuhniy")
                .email("Olga@gmail.com")
                .password("Olga1234$")
                .build();
        UserDTO userDTOAlreadyExist = UserDTO.builder()
                .firstName("Olha")
                .lastName("Konopenko")
                .email("Olga@gmail.com")
                .password("Konopenko1234$")
                .build();
        userService.create(userDTOAlreadyExist);

        ValidationError error = new ValidationError();
        error.setErrors(List.of("User with such email already exists"));
        String expectedResponse = objectMapper.writeValueAsString(error);

        String requestBody = objectMapper.writeValueAsString(userDTO);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DirtiesContext
    public void deleteById_ShouldReturnNoContent() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .firstName("Alex")
                .lastName("Tkach")
                .email("tkach@gmail.com")
                .password("AlexTkach1234$")
                .build();

        User user = userService.create(userDTO);

        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/users/" + user.getId()))
                .andExpect(status().isNoContent())
                .andReturn();

        Assertions.assertThat(mvcResult.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    @DirtiesContext
    public void deleteById_WhenUserNotFound_ShouldReturn404() throws Exception {
        ValidationError error = new ValidationError();
        error.setErrors(List.of("User with such id not found"));
        String expectedResponse = objectMapper.writeValueAsString(error);

        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/users/0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DirtiesContext
    public void edit_returnChangedUser() throws Exception{
        UserDTO userDTOAlreadyExist = UserDTO.builder()
                .firstName("Olga")
                .lastName("Buzoda")
                .email("buzova@gmail.com")
                .password("Buzova1234$")
                .build();

        User userAlreadyExist = userService.create(userDTOAlreadyExist);

        UserDTO userDTOReceived = UserDTO.builder()
                .id(userAlreadyExist.getId())
                .firstName("Olha")
                .lastName("Buzoda")
                .email("buzova@gmail.com")
                .password("Buzova12345$")
                .build();
        String requestBody = objectMapper.writeValueAsString(userDTOReceived);

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(actualResponse).isEqualTo(requestBody);
    }

    @Test
    @DirtiesContext
    public void edit_WhenUserWithSuchEmailAlreadyExist_return409() throws Exception{
        UserDTO userDTOAlreadyExist = UserDTO.builder()
                .firstName("Misha")
                .lastName("Ivanov")
                .email("mishanya@gmail.com")
                .password("Mishanya1234$")
                .build();

        User userAlreadyExist = userService.create(userDTOAlreadyExist);

        UserDTO userDTOAlreadyExistWithSuchEmail = UserDTO.builder()
                .firstName("Misha")
                .lastName("Shemechko")
                .email("mh.boy@gmail.com")
                .password("Shemechko1234$")
                .build();

        User userAlreadyExistWithSuchEmail = userService.create(userDTOAlreadyExistWithSuchEmail);

        UserDTO userDTOReceived = UserDTO.builder()
                .id(userAlreadyExist.getId())
                .firstName("Misha")
                .lastName("Ivanov")
                .email("mh.boy@gmail.com")
                .password("Mishanya1234$")
                .build();

        String requestBody = objectMapper.writeValueAsString(userDTOReceived);

        ValidationError error = new ValidationError();
        error.setErrors(List.of("User with such email already exists"));
        String expectedResponse = objectMapper.writeValueAsString(error);

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DirtiesContext
    public void edit_WhenUserDTOInvalid_return400() throws Exception{
        UserDTO userDTOAlreadyExist = UserDTO.builder()
                .firstName("Misha")
                .lastName("Ivanov")
                .email("mishanya@gmail.com")
                .password("Mishanya1234$")
                .build();

        User userAlreadyExist = userService.create(userDTOAlreadyExist);

        UserDTO userDTOReceived = UserDTO.builder()
                .id(userAlreadyExist.getId())
                .lastName("Ivanov")
                .email("mh.boy@gmail.com")
                .password("Mishanya1234$")
                .build();

        String requestBody = objectMapper.writeValueAsString(userDTOReceived);

        ValidationError error = new ValidationError();
        error.setErrors(List.of("Invalid firstName field: The field firstName should not contain digits or symbols and must not be empty"));
        String expectedResponse = objectMapper.writeValueAsString(error);

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DirtiesContext
    public void edit_WhenUserDTOWithSuchIdNotFound_return404() throws Exception{
        UserDTO userDTOReceived = UserDTO.builder()
                .id(100L)
                .firstName("Misha")
                .lastName("Ivanov")
                .email("mh.boy@gmail.com")
                .password("Mishanya1234$")
                .build();

        String requestBody = objectMapper.writeValueAsString(userDTOReceived);

        ValidationError error = new ValidationError();
        error.setErrors(List.of("User with such id not found"));
        String expectedResponse = objectMapper.writeValueAsString(error);

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

}