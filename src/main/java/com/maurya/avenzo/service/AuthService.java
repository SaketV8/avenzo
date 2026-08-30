package com.maurya.avenzo.service;

import com.maurya.avenzo.dto.request.LoginUserRequestDto;
import com.maurya.avenzo.dto.request.RegisterUserRequestDto;
import com.maurya.avenzo.dto.response.LoginUserResponseDto;
import com.maurya.avenzo.dto.response.PrivateUserResponseDto;
import com.maurya.avenzo.dto.response.RegisterUserResponseDto;
import com.maurya.avenzo.entity.SessionEntity;
import com.maurya.avenzo.entity.UserEntity;
import com.maurya.avenzo.exception.ApiException;
import com.maurya.avenzo.exception.ErrorCode;
import com.maurya.avenzo.repository.SessionRepository;
import com.maurya.avenzo.repository.UserRepository;
import com.maurya.avenzo.security.CustomUserDetails;
import com.maurya.avenzo.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final SessionRepository sessionRepository;

    /*
    private void setCookie(
            HttpServletResponse response,
            String name,
            String value,
            int maxAge
    ) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // false for local http
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }
    */

    private void setCookie(
            HttpServletResponse response,
            String name,
            String value,
            long maxAge
    ) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true) // false for local http
//                .sameSite("Lax")
                .sameSite("None")
                .path("/")
//                .domain(".avenzo.localhost")
                .maxAge(maxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private String hash(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(token.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public RegisterUserResponseDto registerUser(RegisterUserRequestDto registerUserRequestDto) {
        log.info("Started Registering User: {}", registerUserRequestDto.getName());
        try {
            UserEntity user = new UserEntity();
            user.setName(registerUserRequestDto.getName());
            user.setEmail(registerUserRequestDto.getEmail());
            user.setPassword(passwordEncoder.encode(registerUserRequestDto.getPassword()));
            user.setPhone(registerUserRequestDto.getPhone());
            user.setProfilePicture(registerUserRequestDto.getProfilePicture());
            UserEntity registeredUser = userRepository.save(user);

            RegisterUserResponseDto response = new RegisterUserResponseDto(registeredUser.getId(), registeredUser.getName(), registeredUser.getEmail());

            log.info("Successfully Registered User: {}", registerUserRequestDto.getName());

            return response;
        } catch (DataIntegrityViolationException ex) {
            log.error("Failed to register user. Name: {}", registerUserRequestDto.getName(), ex);
            throw new ApiException(ErrorCode.USER_ALREADY_EXISTS);
        } finally {
            log.info("Completed Registering User request: {}", registerUserRequestDto.getName());
        }
    }

    public LoginUserResponseDto loginUser(LoginUserRequestDto loginUserRequestDto, HttpServletResponse response) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserRequestDto.getEmail(), loginUserRequestDto.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            UserEntity user = Objects.requireNonNull(userDetails).getUserEntity();

            // Access Token (JWT Token)
            String accessToken = jwtService.generateToken(Objects.requireNonNull(userDetails));

            // Random Refresh Token
            String refreshToken = UUID.randomUUID().toString();

            // Save Session
            SessionEntity session = new SessionEntity();
            session.setUser(user);
            session.setRefreshTokenHash(hash(refreshToken));
            session.setExpiresAt(LocalDateTime.now().plusDays(30));

            sessionRepository.save(session);

            // Cookie
            setCookie(response, "refreshToken", refreshToken, 60 * 60 * 24 * 30);

            return new LoginUserResponseDto(accessToken);
        } catch (UsernameNotFoundException ex) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        } catch (BadCredentialsException ex) {
            throw new ApiException(ErrorCode.INVALID_CREDENTIALS);
        }

    }

    public LoginUserResponseDto refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            log.error("🐸🐸 Refresh failed: refreshToken cookie not found.");
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }

        SessionEntity session = sessionRepository
                .findByRefreshTokenHash(hash(refreshToken))
                .orElseThrow(() -> {
                    log.error("🐸🐸 Refresh failed: no session found for refresh token.");
                    return new ApiException(ErrorCode.INVALID_TOKEN);
                });

        if (session.isRevoked() ||
                session.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.error("🐸🐸  Refresh failed: session is revoked. Session ID = " + session.getId());
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }

        // Rotate Refresh Token
        String newRefreshToken = UUID.randomUUID().toString();

        session.setRefreshTokenHash(hash(newRefreshToken));
        session.setExpiresAt(LocalDateTime.now().plusDays(30));

        sessionRepository.save(session);

        setCookie(response, "refreshToken", newRefreshToken, 60 * 60 * 24 * 30);

        CustomUserDetails userDetails =
                new CustomUserDetails(session.getUser());

        String accessToken = jwtService.generateToken(userDetails);

        log.info("🐸🐸 /refresh - accessToken: " + accessToken);

        return new LoginUserResponseDto(accessToken);
    }

    public void logout(HttpServletRequest request,
                       HttpServletResponse response) {

        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken != null) {
            sessionRepository.findByRefreshTokenHash(hash(refreshToken))
                    .ifPresent(sessionRepository::delete);
        }

        setCookie(response, "refreshToken", "", 0);
    }

    public PrivateUserResponseDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // this authentication is returning the UserDetails and in our CustomUserDetails Implementation
        // we have added the method getName(), which will eventually return the Email
        /*String email = Objects.requireNonNull(authentication).getName();*/

        /*UserDetails userDetails = (UserDetails) Objects.requireNonNull(authentication).getPrincipal();*/

        // NOTE:
        // to avoid the user query for this scenario, we can directly access the whole UserEntity
        CustomUserDetails userDetails = (CustomUserDetails) Objects.requireNonNull(authentication).getPrincipal();

        UserEntity user = Objects.requireNonNull(userDetails).getUserEntity();

        return new PrivateUserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getProfilePicture(),
                user.getRole()
        );
    }
}
