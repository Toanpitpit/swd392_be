package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.dto.request.LoginRequest;
import fa.training.car_rental_management.dto.response.LoginResponse;
import fa.training.car_rental_management.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Xử lý login và tạo JWT token
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Login endpoint
     * POST /auth/login
     * 
     * Request body:
     * {
     *   "username": "admin",
     *   "password": "password123"
     * }
     * 
     * Response:
     * {
     *   "success": true,
     *   "message": "Login successful",
     *   "data": {
     *     "token": "eyJhbGc...",
     *     "username": "admin",
     *     "role": "ROLE_ADMIN"
     *   }
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            log.info("Login attempt for user: {}", request.getUsername());

            // Validate request
            if (request.getUsername() == null || request.getUsername().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Username is required"));
            }

            if (request.getPassword() == null || request.getPassword().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Password is required"));
            }

            String role = "CUSTOMER";

            // Support both username and email login
            String username = request.getUsername().toLowerCase();
            
            // Admin user
            if (("admin".equals(username) || "admin@carental.com".equals(username)) && 
                "admin123".equals(request.getPassword())) {
                role = "ADMIN";
            } 
            // Car Owner user
            else if (("car_owner".equals(username) || "owner1@carental.com".equals(username)) && 
                     "owner123".equals(request.getPassword())) {
                role = "CAR_OWNER";
            } 
            // Customer users
            else if (("customer".equals(username) || "customer1@carental.com".equals(username) || 
                      "customer2@carental.com".equals(username)) && 
                     "customer123".equals(request.getPassword())) {
                role = "CUSTOMER";
            } 
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid username or password"));
            }

            // Tạo JWT token
            String token = jwtService.generateToken(request.getUsername(), role);

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .username(request.getUsername())
                    .role(role)
                    .build();

            log.info("User {} logged in successfully with role {}", request.getUsername(), role);

            return ResponseEntity.ok(ApiResponse.success("Login successful", response));

        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyToken(@RequestHeader("Authorization") String bearerToken) {
        try {
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid Authorization header"));
            }

            String token = bearerToken.substring(7);
            
            var usernameOpt = jwtService.validateTokenAndGetUsername(token);
            
            if (usernameOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(
                        "Token is valid",
                        "User: " + usernameOpt.get()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or expired token"));
            }

        } catch (Exception e) {
            log.error("Token verification error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token verification failed"));
        }
    }

    /**
     * Health check
     * GET /auth/ping
     */
    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<String>> ping() {
        return ResponseEntity.ok(ApiResponse.success("Pong", "Auth service is running"));
    }
}

