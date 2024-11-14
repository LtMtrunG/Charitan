package com.example.Chartian.user;

import com.example.Chartian.user.dto.UserDTO;

public interface UserExternalAPI {
    public UserDTO findUserById(Long userId);
    public UserDTO findUserByEmail(String email);
}
