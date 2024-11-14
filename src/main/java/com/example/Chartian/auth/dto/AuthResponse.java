package com.example.Chartian.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    private boolean authenticated;
    private String role;
}
