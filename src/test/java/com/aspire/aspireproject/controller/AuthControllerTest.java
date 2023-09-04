package com.aspire.aspireproject.controller;

import com.aspire.aspireproject.dao.request.SignInRequest;
import com.aspire.aspireproject.dao.request.SignUpRequest;
import com.aspire.aspireproject.dao.response.ErrorResponse;
import com.aspire.aspireproject.dao.response.JwtAuthenticationResponse;
import com.aspire.aspireproject.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignup_Successful() {
        SignUpRequest request =  new SignUpRequest("ABC", "PQR", "abc@gmail.com", "abc", "ADMIN");

        // Mock the service's behavior
        doNothing().when(authService).signup(request);

        // Make the request
        ResponseEntity<?> responseEntity = authController.signup(request);

        // Assertions
        verify(authService, times(1)).signup(request);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Successful", responseEntity.getBody());
    }

    @Test
    public void testSignup_Failure() {
        SignUpRequest request =  new SignUpRequest("ABC", "PQR", "abc@gmail.com", "abc", "ADMIN");

        // Mock the service to throw an exception
        doThrow(new RuntimeException("Failed to sign up")).when(authService).signup(request);

        // Make the request
        ResponseEntity<?> responseEntity = authController.signup(request);

        // Assertions
        verify(authService, times(1)).signup(request);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Failed to sign up: Failed to sign up", errorResponse.getMessage());
    }

    @Test
    public void testSignin_Successful() {
        SignInRequest request = new SignInRequest(/* Set your request data here */);
        JwtAuthenticationResponse expectedResponse = JwtAuthenticationResponse.builder()
                .token("token")
                .build();

        // Mock the service's behavior
        when(authService.signin(request)).thenReturn(expectedResponse);

        // Make the request
        ResponseEntity<?> responseEntity = authController.signin(request);

        // Assertions
        verify(authService, times(1)).signin(request);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof JwtAuthenticationResponse);
        JwtAuthenticationResponse actualResponse = (JwtAuthenticationResponse) responseEntity.getBody();
        assertEquals(expectedResponse.getToken(), actualResponse.getToken());
    }

    @Test
    public void testSignin_Failure() {
        SignInRequest request = new SignInRequest(/* Set your request data here */);

        // Mock the service to throw an exception
        doThrow(new RuntimeException("Failed to sign in")).when(authService).signin(request);

        // Make the request
        ResponseEntity<?> responseEntity = authController.signin(request);

        // Assertions
        verify(authService, times(1)).signin(request);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Failed to sign in: Failed to sign in", errorResponse.getMessage());
    }

    @Test
    public void testLogoutUser() {
        String username = "your-username";

        // Make the request
        ResponseEntity<String> responseEntity = authController.logoutUser(username);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("logged out successfully", responseEntity.getBody());
    }
}
