package com.example.Chartian.donor.controller;

import com.example.Chartian.donor.dto.DonorCreationRequest;
import com.example.Chartian.donor.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/donor")
public class DonorController {
    @Autowired
    private DonorService donorService;

    @PostMapping("/create")
    public ResponseEntity<String> createDonor(@RequestBody @Valid DonorCreationRequest request) {

        try {
            donorService.createDonor(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Donor registered successfully!");
        } catch (ResponseStatusException e) {
            // If the exception is a ResponseStatusException, return the status and message
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }
}
