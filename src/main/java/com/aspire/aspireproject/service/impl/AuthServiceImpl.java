package com.aspire.aspireproject.service.impl;

import com.aspire.aspireproject.dao.request.SignInRequest;
import com.aspire.aspireproject.dao.request.SignUpRequest;
import com.aspire.aspireproject.dao.response.JwtAuthenticationResponse;
import com.aspire.aspireproject.exception.UsernameAlreadyExistsException;
import com.aspire.aspireproject.model.user.Role;
import com.aspire.aspireproject.model.user.User;
import com.aspire.aspireproject.repository.UserRepository;
import com.aspire.aspireproject.service.AuthService;
import com.aspire.aspireproject.service.JwtService;
import com.aspire.aspireproject.service.helper.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private AuthHelper authHelper;

    public boolean isUserAlreadyRegistered(String username){
        return userRepository.existsByUsername(username);
    }

    @Override
    public void signup(SignUpRequest request) {
        authHelper.validateSignupParams(request);

        if (isUserAlreadyRegistered(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already taken.");
        }

        var user = User.builder().firstName(request.getFirstName()).lastName(request.getLastName())
                .username(request.getUsername()).password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole())).build();
        userRepository.save(user);
    }

    @Override
    public JwtAuthenticationResponse signin(SignInRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

}
