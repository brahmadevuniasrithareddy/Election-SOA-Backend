package com.election.authservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.election.authservice.dto.LoginRequest;
import com.election.authservice.dto.SignupRequest;
import com.election.authservice.entity.User;
import com.election.authservice.security.JwtService;
import com.election.authservice.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService,
                          JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signup(
            @RequestBody SignupRequest request) {

        User user = authService.signup(
                request.getFullName(),
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        boolean authenticated = authService.login(
                request.getUsername(),
                request.getPassword()
        );

        if (!authenticated) {
            return ResponseEntity
                    .status(401)
                    .body("Invalid username or password");
        }

        User user = authService.findByUsername(
                request.getUsername()
        );

        String token = jwtService.generateToken(user);

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Login successful");
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("role", user.getRole().name());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(
            org.springframework.security.core.Authentication authentication) {

        String username = authentication.getName();

        User user = authService.findByUsername(username);

        return ResponseEntity.ok(user);
    }
}