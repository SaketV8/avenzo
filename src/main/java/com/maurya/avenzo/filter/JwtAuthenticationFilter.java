package com.maurya.avenzo.filter;

import com.maurya.avenzo.dto.response.exception.ErrorResponseDto;
import com.maurya.avenzo.exception.ApiException;
import com.maurya.avenzo.exception.ErrorCode;
import com.maurya.avenzo.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    // we have done the loadbyusername implementation in CustomUserDetailsService
    private final UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();


        return path.startsWith("/docs/")
                || path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/auth/register")
//                || path.startsWith("/api/v1/events")
                || path.startsWith("/api/v1/auth/refresh");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("🐸 JWT filter started");
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
//            writeError(response, ErrorCode.INVALID_TOKEN);
            return;
        }

        try {
            // extract the token from the header
            String token = header.substring(7);

            // parsing & verifying claims
            Claims claims = jwtService.parseToken(token);


            // email (username here)
            String email = claims.getSubject();

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            // logging the all role -> permission after mapping
            System.out.print("🐸 PERMISSION: ");
            System.out.println(userDetails.getAuthorities());

            // saving the auth in the spring security
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Only continue if auth succeeded
//            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            /*response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);*/
            /*new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);*/

            /*
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            response.getWriter().write("""
                    {
                        "status": 401,
                        "error": "Unauthorized",
                        "message": "Invalid or expired JWT token"
                    }
                    """);
            */
            /*
            ErrorCode error = ErrorCode.INVALID_TOKEN;
            ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                    .code(error.name())
                    .message(error.getMessage())
                    .status(error.getStatus().value())
                    .build();
            response.setStatus(error.getStatus().value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            objectMapper.writeValue(response.getWriter(), errorResponseDto);
            return;
            */
            SecurityContextHolder.clearContext();
            writeError(response, ErrorCode.INVALID_TOKEN);
        }

        // pass the request to next filter
        filterChain.doFilter(request, response);
    }

    // helper function :)
    private void writeError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .status(errorCode.getStatus().value())
                .build();

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
