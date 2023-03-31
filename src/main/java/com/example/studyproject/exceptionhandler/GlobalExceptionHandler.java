package com.example.studyproject.exceptionhandler;


import com.example.studyproject.exceptions.UserAlreadyExistsException;
import com.example.studyproject.exceptions.UserNotFoundException;
import com.example.studyproject.utils.validation.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ValidationError> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return buildValidationErrorResponse(List.of("User with such email already exists"), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ValidationError> handleUserNotFoundException(UserNotFoundException ex) {
        return buildValidationErrorResponse(List.of("User with such id not found"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<String> errors = new ArrayList<>();

        for (FieldError fieldError : fieldErrors) {
            errors.add(fieldError.getDefaultMessage());
        }

        return buildValidationErrorResponse(errors, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ValidationError> buildValidationErrorResponse(List<String> errors, HttpStatus status) {
        ValidationError validationError = new ValidationError();
        validationError.setErrors(errors);
        return new ResponseEntity<>(validationError, status);
    }

}
