package com.flexspace.controller;


import com.flexspace.common.ApiResponse;
import com.flexspace.dto.AuthRequest;
import com.flexspace.dto.UserRegistrationRequest;
import com.flexspace.dto.UserResponse;
import com.flexspace.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse userResponse = userService.register(request);

        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", userResponse));
    }


}
