package com.college.dbmsproject.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // A strong, secret key. In a real app, put this in application.properties
    private final SecretKey jwtSecret = Keys.hmacShaKeyFor("YourVeryLongAndSecureSecretKeyHereThatIsAtLeast256BitsLong".getBytes());

    private final int jwtExpirationMs = 86400000; // 24 hours

    // Generate a token from authentication
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(jwtSecret)
                .compact();
    }

    // Get username from token
    public String getUserNameFromJwtToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Validate the token
    public boolean validateJwtToken(String authToken, UserDetails userDetails) {
        final String username = getUserNameFromJwtToken(authToken);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(authToken));
    }

    // Check if token is expired
    private boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // Helper to get any claim
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                                 .verifyWith(jwtSecret)
                                 .build()
                                 .parseSignedClaims(token)
                                 .getPayload();
        return claimsResolver.apply(claims);
    }
}