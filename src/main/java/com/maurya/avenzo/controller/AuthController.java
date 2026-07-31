package com.maurya.avenzo.controller;

import com.maurya.avenzo.constant.UrlConstant;
import com.maurya.avenzo.dto.request.LoginUserRequestDto;
import com.maurya.avenzo.dto.request.RegisterUserRequestDto;
import com.maurya.avenzo.dto.response.LoginUserResponseDto;
import com.maurya.avenzo.dto.response.RegisterUserResponseDto;
import com.maurya.avenzo.dto.response.PrivateUserResponseDto;
import com.maurya.avenzo.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UrlConstant.BASE_URL + "/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDto> registerUser(@RequestBody RegisterUserRequestDto  registerUserRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(registerUserRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponseDto> loginUser(@Valid @RequestBody LoginUserRequestDto loginUserRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.loginUser(loginUserRequestDto));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PrivateUserResponseDto> getCurrentUser() {
        return ResponseEntity.status(HttpStatus.OK).body(authService.getCurrentUser());
    }
}
