package com.maurya.avenzo.security;

import com.maurya.avenzo.dto.response.exception.ErrorResponseDto;
import com.maurya.avenzo.exception.ErrorCode;
import com.maurya.avenzo.filter.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.databind.ObjectMapper;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {


        http
                // disable CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // TODO:
                // cors (will add it later when required)

                // session -> jwt based auth
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // auth filters
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/docs/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/health").hasAuthority("CHECK_HEALTH")

                        .requestMatchers(HttpMethod.GET, "/api/v1/events/**").permitAll()
                        .requestMatchers("/api/v1/events/**").hasAnyRole("USER", "ADMIN")

                        // for category creation
                        .requestMatchers("/api/v1/category").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex

                        // 401 - Not Authenticated
                        .authenticationEntryPoint((request, response, authException) -> {

                            /*ErrorCode error = ErrorCode.ACCESS_DENIED;*/
                            /*ErrorCode error = ErrorCode.UNAUTHORIZED;*/
                            ErrorCode error = ErrorCode.NOT_AUTHENTICATED;

                            ErrorResponseDto body = ErrorResponseDto.builder()
                                    .code(error.name())
                                    .message(error.getMessage())
                                    .status(error.getStatus().value())
                                    .build();

                            response.setStatus(error.getStatus().value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                            System.out.println("🍃🍃 NOT_AUTHENTICATED");

                            objectMapper.writeValue(response.getWriter(), body);
                        })

                        // 403 - Authenticated but no permission
                        .accessDeniedHandler((request, response, accessDeniedException) -> {

                            /*ErrorCode error = ErrorCode.ACCESS_DENIED;*/
                            /*ErrorCode error = ErrorCode.FORBIDDEN;*/
                            ErrorCode error = ErrorCode.AUTHENTICATED_BUT_NO_PERMISSION;

                            ErrorResponseDto body = ErrorResponseDto.builder()
                                    .code(error.name())
                                    .message(error.getMessage())
                                    .status(error.getStatus().value())
                                    .build();

                            response.setStatus(error.getStatus().value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                            System.out.println("🍃🍃 AUTHENTICATED_BUT_NO_PERMISSION");

                            objectMapper.writeValue(response.getWriter(), body);
                        })
                )


                // jwtAuthenticationFilter here
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
        return authConfig.getAuthenticationManager();
    }
}
