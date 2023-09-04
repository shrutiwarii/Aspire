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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthHelper authHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        authService = new AuthServiceImpl(userRepository, jwtService, passwordEncoder);
        authService.authHelper = authHelper;
    }

    @Test
    void testIsUserAlreadyRegisteredExistingUser() {
        String username = "testuser";
        when(userRepository.existsByUsername(username)).thenReturn(true);
        assertTrue(authService.isUserAlreadyRegistered(username));
    }

    @Test
    void testIsUserAlreadyRegisteredNonExistingUser() {
        String username = "newuser";
        when(userRepository.existsByUsername(username)).thenReturn(false);
        assertFalse(authService.isUserAlreadyRegistered(username));
    }


    @Test
    void testSigninValidUser() {
        SignInRequest request = new SignInRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        JwtAuthenticationResponse response = authService.signin(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
    }

    @Test
    void testSigninInvalidUser() {
        SignInRequest request = new SignInRequest();
        request.setUsername("nonexistentuser");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.signin(request));
    }
}
