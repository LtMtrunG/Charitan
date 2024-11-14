package com.example.Chartian.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AuthRequest {

    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Password is required")
    private String password;
}
