package com.example.Chartian.auth;

import com.example.Chartian.auth.dto.IntrospectTokenRequest;
import com.example.Chartian.auth.dto.IntrospectTokenResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthExternalAPI {
    public IntrospectTokenResponse introspect(IntrospectTokenRequest request) throws JOSEException, ParseException;
}
