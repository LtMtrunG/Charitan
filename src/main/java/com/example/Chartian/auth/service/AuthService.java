package com.example.Chartian.auth.service;

import com.example.Chartian.auth.AuthExternalAPI;
import com.example.Chartian.auth.dto.AuthRequest;
import com.example.Chartian.auth.dto.AuthResponse;
import com.example.Chartian.auth.dto.IntrospectTokenRequest;
import com.example.Chartian.auth.dto.IntrospectTokenResponse;
import com.example.Chartian.user.UserExternalAPI;
import com.example.Chartian.user.dto.UserDTO;
import com.example.Chartian.utils.Role;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthExternalAPI {

    private final UserExternalAPI userExternalAPI;

    @NonFinal
    protected static final String SIGNER_KEY = "WN1p+NNBEUYPdgLAec9Glzja6hTei7ElFAk975/CDLEIy6dmlrwofb4fdNRKuouN";

    public AuthResponse authenticate(AuthRequest request, HttpServletResponse response){
        UserDTO user = userExternalAPI.findUserByEmail(request.getEmail());

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with email not found.");
        }

        if (!user.isVerfied()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User have not verified their account.");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated =  passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!authenticated){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is not correct");
        }

        var token = generateToken(user);

        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true)        // HTTP-only flag
                .secure(true)          // Use secure flag if using HTTPS
                .path("/")             // Cookie available to the entire domain
                .maxAge(6 * 60 * 60) // Set cookie expiration (7 days here)
                .sameSite("None")    // CSRF protection
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setRole(user.getRole().getName());
        authResponse.setAuthenticated(true);

        return authResponse;
    }

    private String generateToken(UserDTO user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .issuer("charitan.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(6, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildScope(UserDTO user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        Role role = user.getRole();
        if (role != null) {
            stringJoiner.add("ROLE_" + role.getName());
            if (!CollectionUtils.isEmpty(role.getPermissions()))
                role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
        }

        return stringJoiner.toString();
    }

    @Override
    public IntrospectTokenResponse introspect(IntrospectTokenRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var valid = signedJWT.verify(verifier);
        IntrospectTokenResponse introspectTokenResponse = new IntrospectTokenResponse();
        introspectTokenResponse.setValid(valid && expirationTime.after(new Date()));
        return introspectTokenResponse;
    }
}
