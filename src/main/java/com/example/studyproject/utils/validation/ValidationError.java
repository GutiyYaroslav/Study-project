package com.example.studyproject.utils.validation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class ValidationError {

    private List<String> errors;
}