package com.example.Chartian.donor.service;

import com.example.Chartian.donor.dto.DonorCreationRequest;
import com.example.Chartian.donor.entity.Donor;
import com.example.Chartian.donor.repository.DonorRepository;
import com.example.Chartian.user.UserExternalAPI;
import com.example.Chartian.user.dto.UserCreationRequest;
import com.example.Chartian.user.dto.UserDTO;
import com.example.Chartian.user.entity.User;
import com.example.Chartian.user.repository.UserRepository;
import com.example.Chartian.utils.Role;
import com.example.Chartian.utils.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class DonorService {
    @Autowired
    private DonorRepository donorRepository;
    private final UserExternalAPI userExternalAPI;

    public void createDonor(DonorCreationRequest request) {
        UserDTO userDTO = userExternalAPI.findUserById(request.getUserId());

        if (userDTO == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not found.");
        }

        if (donorRepository.existsById(userDTO.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Donor is already created.");
        }

        if (!userDTO.isVerfied()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not verified.");
        }

        if (!userDTO.getRole().getName().equals("DONOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not donor.");
        }

        Donor donor = new Donor();
        donor.setUserId(userDTO.getId());
        donor.setFirstName(request.getFirstName());
        donor.setLastName(request.getLastName());
        donor.setAddress(request.getAddress());

        donorRepository.save(donor);
    }
}
