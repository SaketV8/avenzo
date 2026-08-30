//package com.maurya.avenzo.filter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@Slf4j
//public class RequestLoggingFilter extends OncePerRequestFilter {
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//        log.info("\uD83E\uDD84\uD83E\uDD84 {} {} [{}]", request.getMethod(), request.getRequestURI(), response.getStatus());
//
//        filterChain.doFilter(request, response);
//    }
//}

package com.maurya.avenzo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String RESET = "\033[0m";

    private static final String BLUE = "\033[34m";
    private static final String GREEN = "\033[32m";
    private static final String YELLOW = "\033[33m";
    private static final String RED = "\033[31m";
    private static final String MAGENTA = "\033[35m";
    private static final String CYAN = "\033[36m";

    private static final String BLUE_BG = "\033[44m";
    private static final String GREEN_BG = "\033[42m";
    private static final String YELLOW_BG = "\033[43m";
    private static final String RED_BG = "\033[41m";
    private static final String MAGENTA_BG = "\033[45m";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } finally {

            String method = request.getMethod();
            String color = getMethodColor(method);

            log.info(
                    "|{} {} {}| {} [{}{}{}]",
                    color,
                    method,
                    RESET,
                    request.getRequestURI(),
                    CYAN,
                    response.getStatus(),
                    RESET
            );
        }
    }

    private String getMethodColor(String method) {
        return switch (method) {
            case "GET" -> BLUE_BG;
            case "POST" -> GREEN_BG;
            case "PUT" -> YELLOW_BG;
            case "DELETE" -> RED_BG;
            case "PATCH" -> MAGENTA_BG;
            default -> BLUE;
        };
    }
}