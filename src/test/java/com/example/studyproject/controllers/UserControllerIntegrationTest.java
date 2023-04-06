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
        User user = userService.create(createDefaultUserDTO());

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
        User user = User.builder()
                .id(1L)
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@domain.com")
                .password("Password1#")
                .build();

        String requestBody = objectMapper.writeValueAsString(createDefaultUserDTO());
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
                .lastName("LastName")
                .email("email@domain.com")
                .password("Password1#")
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
        userService.create(createDefaultUserDTO());

        ValidationError error = new ValidationError();
        error.setErrors(List.of("User with such email already exists"));
        String expectedResponse = objectMapper.writeValueAsString(error);

        String requestBody = objectMapper.writeValueAsString(createDefaultUserDTO());

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
        User user = userService.create(createDefaultUserDTO());

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
        User userAlreadyExist = userService.create(createDefaultUserDTO());

        UserDTO userDTOReceived = UserDTO.builder()
                .id(userAlreadyExist.getId())
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@domain.com")
                .password("Password1#")
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
        User userAlreadyExist = userService.create(createDefaultUserDTO());

        UserDTO userDTOAlreadyExistWithSuchEmail = UserDTO.builder()
                .firstName("FirstName1")
                .lastName("LastName1")
                .email("email1@domain.com")
                .password("Password1#")
                .build();

        User userAlreadyExistWithSuchEmail = userService.create(userDTOAlreadyExistWithSuchEmail);

        UserDTO userDTOReceived = UserDTO.builder()
                .id(userAlreadyExist.getId())
                .firstName("FirstName")
                .lastName("LastName")
                .email("email1@domain.com")
                .password("Password1#")
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
        User userAlreadyExist = userService.create(createDefaultUserDTO());

        UserDTO userDTOReceived = UserDTO.builder()
                .id(userAlreadyExist.getId())
                .lastName("LastName")
                .email("email@domain.com")
                .password("Password1#")
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
                .id(1L)
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@domain.com")
                .password("Password1#")
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

    private UserDTO createDefaultUserDTO(){
        return UserDTO.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@domain.com")
                .password("Password1#")
                .build();
    }

}