package com.maurya.avenzo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "User not found. 👾"
    ),

    USER_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "User already exists, try different email & phone number"
    ),

    EMAIL_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "Email is already registered."
    ),

    CATEGORY_DOES_NOT_EXIST(
            HttpStatus.NOT_FOUND,
            "Category doesn't exist."
    ),

    EVENT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "Event not found."
    ),

    EVENT_FULL(
            HttpStatus.CONFLICT,
            "Event is full."
    ),

    CAPACITY_CANNOT_BE_REDUCED(
            HttpStatus.CONFLICT,
            "Capacity cannot be less than already registered participants."
    ),
    EVENT_MEMBER_ALREADY_EXISTS(
            HttpStatus.NOT_FOUND,
            "Event member already exists."
    ),

    EVENT_MEMBER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "Event member not found."
    ),

    ALREADY_ATTENDED_EVENT(
            HttpStatus.CONFLICT,
            "Already attended an event."
    ),

    ALREADY_REGISTERED(
            HttpStatus.CONFLICT,
            "Already registered."
    ),

    REGISTRATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "Registration not found."
    ),

    REGISTRATION_CLOSED(
            HttpStatus.BAD_REQUEST,
            "Registration has closed."
    ),


    INVALID_CREDENTIALS(
            HttpStatus.UNAUTHORIZED,
            "Invalid email or password."
    ),

    INVALID_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "Invalid token."
    ),

    NOT_AUTHENTICATED(
            HttpStatus.UNAUTHORIZED,
            "Not authenticated."
    ),

    AUTHENTICATED_BUT_NO_PERMISSION(
            HttpStatus.FORBIDDEN,
            "Not enough permissions."
    ),

    ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "Access denied."
    ),


    VALIDATION_ERROR(
            HttpStatus.BAD_REQUEST,
            "Validation failed."
    ),


    DATABASE_ERROR(
            HttpStatus.CONFLICT,
            "Database operation failed."
    ),


    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Something went wrong."
    );

    private final HttpStatus status;
    private final String message;
}