package com.aspire.aspireproject.controller;

import com.aspire.aspireproject.dao.request.SignInRequest;
import com.aspire.aspireproject.dao.request.SignUpRequest;
import com.aspire.aspireproject.dao.response.ErrorResponse;
import com.aspire.aspireproject.dao.response.JwtAuthenticationResponse;
import com.aspire.aspireproject.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aspire/v1/auth")
public class AuthController {

    @Autowired
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    synchronized public ResponseEntity<? > signup(@RequestBody SignUpRequest request) {
        try{
            authService.signup(request);
            return ResponseEntity.ok("Successful");
        }catch (Exception e){
            ErrorResponse response = ErrorResponse.builder()
                    .message("Failed to sign up: "+e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SignInRequest request) {
        try {
            JwtAuthenticationResponse response = authService.signin(request);
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            ErrorResponse response = ErrorResponse.builder()
                    .message("Failed to sign in: "+e.getMessage())
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<String> logoutUser(String username){
        return ResponseEntity.ok("logged out successfully");
    }
}
