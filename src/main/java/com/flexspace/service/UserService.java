package com.flexspace.service;

import com.flexspace.common.exception.BadRequestException;
import com.flexspace.dto.UserRegistrationRequest;
import com.flexspace.dto.UserResponse;
import com.flexspace.model.User;
import com.flexspace.repository.UserRepository;
import com.flexspace.security.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil ;


    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new BadRequestException("Email already exists");
                });

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setActive(true);

        User savedUser = userRepository.save(user);
        if (savedUser.getId() == null) {
            throw new RuntimeException("User registration failed");
        }


        UserResponse userResponse = new UserResponse(savedUser);
        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail(), "USER");
        userResponse.setToken(token);


        return userResponse;
    }


}
