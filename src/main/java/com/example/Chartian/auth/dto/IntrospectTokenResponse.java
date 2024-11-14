package com.example.Chartian.auth.dto;

public class IntrospectTokenResponse {
    private boolean valid;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}