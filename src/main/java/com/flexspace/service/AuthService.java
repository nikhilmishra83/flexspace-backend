package com.flexspace.service;

import com.flexspace.common.exception.BadRequestException;
import com.flexspace.dto.AuthRequest;
import com.flexspace.dto.AuthResponse;
import com.flexspace.model.User;
import com.flexspace.repository.UserRepository;
import com.flexspace.security.JwtFilter;
import com.flexspace.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }


    public AuthResponse login(AuthRequest request) {
        String email = request.getEmail();
        log.info("Login attempt for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed - user not found: {}", email);
                    return new BadRequestException("User not found");
                });
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for email: {}", email);
            throw new BadRequestException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
        log.info("Login successful for userId: {}", user.getId());
        return new AuthResponse(token);
    }


}