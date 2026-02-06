package com.atamosa.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atamosa.backend.config.JwtUtil;
import com.atamosa.backend.dto.AuthRequest;
import com.atamosa.backend.dto.AuthResponse;
import com.atamosa.backend.dto.RegisterRequest;
import com.atamosa.backend.entity.UserEntity;
import com.atamosa.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already taken!");
        }
        
        // Create new user
        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        
        userRepository.save(user);
        
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: Invalid email or password!");
        }
        
        final UserDetails userDetails = userRepository.findByEmail(request.getEmail())
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .authorities("USER")
                        .build())
                .orElseThrow();
        
        final String token = jwtUtil.generateToken(userDetails);
        
        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        
        return ResponseEntity.ok(response);
    }
}