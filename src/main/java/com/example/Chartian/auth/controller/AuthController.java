package com.example.Chartian.auth.controller;

import com.example.Chartian.auth.dto.AuthRequest;
import com.example.Chartian.auth.dto.AuthResponse;
import com.example.Chartian.auth.dto.IntrospectTokenRequest;
import com.example.Chartian.auth.dto.IntrospectTokenResponse;
import com.example.Chartian.auth.service.AuthService;
import com.example.Chartian.utils.ErrorResponse;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    ResponseEntity<Object> authenticate(@RequestBody @Valid AuthRequest request, HttpServletResponse response){
        try {
            AuthResponse authResponse = authService.authenticate(request, response);
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        } catch (ResponseStatusException e) {
            // If the exception is a ResponseStatusException, return the status and message
            ErrorResponse errorResponse = new ErrorResponse(e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        } catch (Exception e) {
            // Handle other exceptions
            ErrorResponse errorResponse = new ErrorResponse("An error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/introspect")
    ResponseEntity<Object> introspect(@RequestBody @Valid IntrospectTokenRequest request) throws ParseException, JOSEException {
        try {
            IntrospectTokenResponse response = authService.introspect(request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ResponseStatusException e) {
            // If the exception is a ResponseStatusException, return the status and message
            ErrorResponse errorResponse = new ErrorResponse(e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        } catch (Exception e) {
            // Handle other exceptions
            ErrorResponse errorResponse = new ErrorResponse("An error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
