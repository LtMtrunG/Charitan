package com.example.Chartian.user.dto;

import com.example.Chartian.user.entity.User;
import com.example.Chartian.utils.Role;
import lombok.Getter;

@Getter
public class UserDTO {

    private final Long id;
    private final String email;
    private final String password;
    private final boolean isVerfied;
    private final Role role;

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.isVerfied = user.isVerified();
        this.role = user.getRole();
    }
}
