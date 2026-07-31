package com.maurya.avenzo.service;

import com.maurya.avenzo.dto.request.LoginUserRequestDto;
import com.maurya.avenzo.dto.request.RegisterUserRequestDto;
import com.maurya.avenzo.dto.response.LoginUserResponseDto;
import com.maurya.avenzo.dto.response.PrivateUserResponseDto;
import com.maurya.avenzo.dto.response.RegisterUserResponseDto;
import com.maurya.avenzo.entity.UserEntity;
import com.maurya.avenzo.exception.ApiException;
import com.maurya.avenzo.exception.ErrorCode;
import com.maurya.avenzo.repository.UserRepository;
import com.maurya.avenzo.security.CustomUserDetails;
import com.maurya.avenzo.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public RegisterUserResponseDto registerUser(RegisterUserRequestDto registerUserRequestDto) {

        UserEntity user = new UserEntity();
        user.setName(registerUserRequestDto.getName());
        user.setEmail(registerUserRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserRequestDto.getPassword()));
        user.setPhone(registerUserRequestDto.getPhone());
        user.setProfilePicture(registerUserRequestDto.getProfilePicture());

        /*UserEntity registeredUser = userRepository.save(user);*/

        try {
            UserEntity registeredUser = userRepository.save(user);
            return new RegisterUserResponseDto(registeredUser.getId(), registeredUser.getName(), registeredUser.getEmail());
        } catch (DataIntegrityViolationException ex) {
            throw new ApiException(ErrorCode.USER_ALREADY_EXISTS);
        }

        /*return new RegisterUserResponseDto(registeredUser.getId(), registeredUser.getName(), registeredUser.getEmail());*/

    }

    public LoginUserResponseDto loginUser(LoginUserRequestDto loginUserRequestDto) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserRequestDto.getEmail(), loginUserRequestDto.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // JWT Token
            String JwtToken = jwtService.generateToken(Objects.requireNonNull(userDetails));

            return new LoginUserResponseDto(JwtToken);
        } catch (UsernameNotFoundException ex) {
            /*throw new UsernameNotFoundException("User does not exist");*/
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        } catch (BadCredentialsException ex) {
            /*throw new InvalidCredentialsExcepti("Invalid email or password");*/
            throw new ApiException(ErrorCode.INVALID_CREDENTIALS);
        }

    }

    public PrivateUserResponseDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // this authentication is returning the UserDetails and in our CustomUserDetails Implementation
        // we have added the the method getName(), which will eventually return the Email
        /*String email = Objects.requireNonNull(authentication).getName();*/

        /*UserDetails userDetails = (UserDetails) Objects.requireNonNull(authentication).getPrincipal();*/

        // NOTE:
        // to avoid the user query for this scenerio, we can directly access the whole UserEntity
        CustomUserDetails userDetails = (CustomUserDetails) Objects.requireNonNull(authentication).getPrincipal();

        UserEntity user = userDetails.getUserEntity();

        return new PrivateUserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getProfilePicture()
        );
    }
}
