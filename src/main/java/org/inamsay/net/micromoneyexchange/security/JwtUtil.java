package org.inamsay.net.micromoneyexchange.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


/** * Utility class for generating and validating JWT tokens.
 * This class provides methods to create a JWT token with a username and role,
 * and to validate the token and extract claims from it.
 */
@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Generates a JWT token with the given username and role.
     *
     * @param username The username to include in the token.
     * @param role The role to include in the token.
     * @return The generated JWT token as a String.
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 600000)) // 10 mnts
                .signWith(key)
                .compact();
    }
    /**
     * Validates the given JWT token and extracts claims from it.
     *
     * @param token The JWT token to validate.
     * @return The claims extracted from the token.
     * @throws JwtException If the token is invalid or expired.
     */
    public Claims validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
