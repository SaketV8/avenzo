package com.maurya.avenzo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String JTW_SECRETE;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(JTW_SECRETE.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(CustomUserDetails userDetails) {

        return Jwts.builder()
                .subject(userDetails.getUsername())

                // deprecated
                /*.setSubject(userDetails.getUsername())*/

                /* .claim("role", userDetails.getUserEntity().getRole()) */
                /* no need of role in the jwt token, as we are getting role from the db */
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(getKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build().parseSignedClaims(token)
                .getPayload();
    }
}
