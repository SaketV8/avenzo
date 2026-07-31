package com.maurya.avenzo.controller;

import com.maurya.avenzo.constant.UrlConstant;
import com.maurya.avenzo.dto.response.PrivateUserResponseDto;
import com.maurya.avenzo.service.AuthService;
import com.maurya.avenzo.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(UrlConstant.BASE_URL + "/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<PrivateUserResponseDto> getCurrentUser() {
        return ResponseEntity.status(HttpStatus.OK).body(authService.getCurrentUser());
    }

    @GetMapping()
    public ResponseEntity<List<PrivateUserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
