package com.flexspace.dto;

import com.flexspace.model.User;
import lombok.Data;

@Data
public class UserResponse {

    private Long userId;
    private String name;
    private String email;
    private String role;
    private boolean active;
    private String token;

    public UserResponse(User user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.active = user.isActive();
    }

}
