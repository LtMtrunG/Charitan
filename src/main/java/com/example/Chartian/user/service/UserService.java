package com.example.Chartian.user.service;

import com.example.Chartian.user.UserExternalAPI;
import com.example.Chartian.user.dto.UserCreationRequest;
import com.example.Chartian.user.dto.UserDTO;
import com.example.Chartian.user.dto.UserAuthDTO;
import com.example.Chartian.user.entity.User;
import com.example.Chartian.user.repository.UserRepository;
import com.example.Chartian.utils.Role;
import com.example.Chartian.utils.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Transactional
@Service
public class UserService implements UserExternalAPI {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public UserAuthDTO createUser(UserCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use.");
        }

        if (!request.getConfirmPassword().equals(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords are not matched.");
        }

        Optional<Role> role = roleRepository.findByName(request.getRole());
        if (role.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role.");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        User user = new User();
        user.setEmail(request.getEmail());
        user.setVerified(false);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role.get());
        userRepository.save(user);

        return new UserAuthDTO(user);
    }

    @Override
    public UserDTO findUserById(Long userId) {
        Optional<User> user = userRepository.findUserById(userId);
        return user.map(UserDTO::new)
                .orElse(null);
    }

    @Override
    public UserDTO findUserByEmail(String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        return user.map(UserDTO::new)
                .orElse(null);
    }

}
