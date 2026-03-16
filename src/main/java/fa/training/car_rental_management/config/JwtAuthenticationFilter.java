package fa.training.car_rental_management.config;

import fa.training.car_rental_management.util.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * JWT Authentication Filter
 * Xác thực JWT token từ Authorization header
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractTokenFromRequest(request);

            // Nếu token tồn tại và chưa có Authentication trong SecurityContext
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Lấy username từ JWT
                Optional<String> usernameOpt = jwtService.validateTokenAndGetUsername(token);

                if (usernameOpt.isPresent()) {
                    String username = usernameOpt.get();

                    // Lấy role từ JWT
                    Optional<String> roleOpt = jwtService.extractRole(token);
                    String role = roleOpt.orElse("GUEST");

                    // Tạo authorities từ role (exact match, không add prefix)
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority(role)
                    );

                    log.debug("Role from JWT: {}, Authority: {}", role, authorities.get(0).getAuthority());

                    // Tạo authentication token
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    authorities
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("JWT token validated for user: {}, role: {}", username, role);

                } else {
                    log.debug("Invalid or expired JWT token");
                }
            }

        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Skip JWT filter for public endpoints
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        return path.startsWith("/api/auth/")
                || path.startsWith("/api/vehicles")
                || path.startsWith("/api/public")
                || path.equals("/api/error")
                || path.startsWith("/api/actuator/health")
                || path.equals("/api/ping");
    }
}

