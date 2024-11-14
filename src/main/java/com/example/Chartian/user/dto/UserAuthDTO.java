package com.example.Chartian.user.dto;

import com.example.Chartian.user.entity.User;
import lombok.Getter;

@Getter
public class UserAuthDTO {

    private final Long id;
    private final String email;
    private final boolean isVerfied;
    private String role;

    public UserAuthDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.isVerfied = user.isVerified();
        this.role = user.getRole().getName();
    }

}
