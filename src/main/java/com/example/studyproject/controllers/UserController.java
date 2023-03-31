package com.example.studyproject.controllers;

import com.example.studyproject.dto.UserDTO;
import com.example.studyproject.models.User;
import com.example.studyproject.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<User> getById(@PathVariable Long userId){
        return new ResponseEntity<>(userService.getById(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> add(@Valid @RequestBody  UserDTO userDTO){
        return new ResponseEntity<>(userService.create(userDTO), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> edit(@Valid @RequestBody UserDTO userDTO){
        return new ResponseEntity<>(userService.edit(userDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId){
        userService.deleteById(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
