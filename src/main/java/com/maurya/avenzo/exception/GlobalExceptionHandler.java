package com.maurya.avenzo.exception;

import com.maurya.avenzo.dto.response.exception.ErrorResponseDto;
import com.maurya.avenzo.dto.response.exception.ValidationErrorResponseDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseDto> handleApiException(ApiException exception) {
        ErrorCode error = exception.getErrorCode();

        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .code(error.name())
                .message(error.getMessage())
                .status(error.getStatus().value())
                .build();

        return ResponseEntity.status(error.getStatus()).body(errorResponseDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<ValidationErrorResponseDto> validationErrors = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ValidationErrorResponseDto(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .toList();

        // validation related method automatic throw these MethodArgumentNotValidException
        // so, we have this enum, hardcoded here instead of passing via the parameter
        ErrorCode error = ErrorCode.VALIDATION_ERROR;

        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                /*
                .code(ErrorCode.VALIDATION_ERROR.name())
                .message(ErrorCode.VALIDATION_ERROR.getMessage())
                .status(ErrorCode.VALIDATION_ERROR.getStatus().value())
                .errors(errors)
                .build();
                */
                .code(error.name())
                .message(error.getMessage())
                .status(error.getStatus().value())
                .errors(validationErrors)
                .build();

        return ResponseEntity.status(error.getStatus()).body(errorResponseDto);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDatabaseException(
            DataIntegrityViolationException exception
    ) {

        // db related method automatic throw these DataIntegrityException
        // so, we have this enum, hardcoded here instead of passing via the parameter
        ErrorCode error = ErrorCode.DATABASE_ERROR;

        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .code(error.name())
                .message(error.getMessage())
                .status(error.getStatus().value())
                .build();

        return ResponseEntity
                .status(error.getStatus())
                .body(errorResponseDto);
    }

    /*@ExceptionHandler(RuntimeException.class)*/
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(
            Exception exception
            /*RuntimeException exception*/
    ) {

        exception.printStackTrace();

        // java method automatic throw these Exception (these are parent exception, like
        // if not other exception catch it, then that will be caught here
        // so, we have this enum, hardcoded here instead of passing via the parameter
        ErrorCode error = ErrorCode.INTERNAL_SERVER_ERROR;

        ErrorResponseDto response = ErrorResponseDto.builder()
                .code(error.name())
                .message(error.getMessage())
                .status(error.getStatus().value())
                .build();

        return ResponseEntity
                .status(error.getStatus())
                .body(response);
    }

}
