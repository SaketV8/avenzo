package com.maurya.avenzo.dto.response.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationErrorResponseDto {
    private String field;
    private String message;
}
