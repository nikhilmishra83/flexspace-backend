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



}
