package com.maurya.avenzo.dto.response.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {
    private String code;

    private String message;

    private Integer status;

    private List<ValidationErrorResponseDto> errors;
}
