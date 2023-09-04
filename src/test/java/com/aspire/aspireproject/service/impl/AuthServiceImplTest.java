package com.aspire.aspireproject.service.impl;

import com.aspire.aspireproject.dao.request.SignInRequest;
import com.aspire.aspireproject.dao.request.SignUpRequest;
import com.aspire.aspireproject.dao.response.JwtAuthenticationResponse;
import com.aspire.aspireproject.exception.UsernameAlreadyExistsException;
import com.aspire.aspireproject.model.user.Role;
import com.aspire.aspireproject.model.user.User;
import com.aspire.aspireproject.repository.UserRepository;
import com.aspire.aspireproject.service.JwtService;
import com.aspire.aspireproject.service.helper.AuthHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(userRepository, jwtService, passwordEncoder);
    }


//    @Test
//    void testSignup_UsernameAlreadyExists() {
//        SignUpRequest signUpRequest = new SignUpRequest("John", "Doe", "john@example.com", "password", "USER");
//
//        // Mock userRepository.existsByUsername
//        when(userRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(true);
//
//        assertThrows(UsernameAlreadyExistsException.class, () -> authService.signup(signUpRequest));
//    }

    @Test
    void testSignin() {
        SignInRequest signInRequest = new SignInRequest("john@example.com", "password");
        User user =  User.builder()
                .firstName("John")
                .lastName("Doe")
                .role(Role.CUSTOMER)
                .username("John@abc.com")
                .password("Test")
                .build();

        when(userRepository.findByUsername(signInRequest.getUsername())).thenReturn(java.util.Optional.of(user));

        // Mock jwtService.generateToken
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        JwtAuthenticationResponse response = authService.signin(signInRequest);
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
    }

    @Test
    void testSignin_InvalidCredentials() {
        SignInRequest signInRequest = new SignInRequest("john@example.com", "password");

        // Mock userRepository.findByUsername to return an empty Optional
        when(userRepository.findByUsername(signInRequest.getUsername())).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.signin(signInRequest));
    }
}
