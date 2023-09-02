package com.aspire.aspireproject.service;

import com.aspire.aspireproject.dao.request.SignInRequest;
import com.aspire.aspireproject.dao.request.SignUpRequest;
import com.aspire.aspireproject.dao.response.JwtAuthenticationResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    void signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SignInRequest request);
}