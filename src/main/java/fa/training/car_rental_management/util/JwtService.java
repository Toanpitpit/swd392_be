package fa.training.car_rental_management.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

/**
 * JWT Service
 * Tạo, validate, và extract thông tin từ JWT token
 */
@Slf4j
@Component
public class JwtService {

    @Value("${jwt.secret:your_secret_key_here_min_32_chars_required_for_security}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Tạo JWT token
     */
    public String generateToken(String username, String role) {
        try {
            return Jwts.builder()
                    .setSubject(username)
                    .claim("role", role)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token", e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    /**
     * Validate token và lấy username
     */
    public Optional<String> validateTokenAndGetUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return Optional.of(claims.getSubject());
        } catch (Exception e) {
            log.debug("Invalid or expired JWT token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Extract role từ token
     */
    public Optional<String> extractRole(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return Optional.ofNullable((String) claims.get("role"));
        } catch (Exception e) {
            log.debug("Error extracting role from token: {}", e.getMessage());
            return Optional.empty();
        }
    }
}

