package com.aspire.aspireproject.controller;

import com.aspire.aspireproject.dao.request.SignInRequest;
import com.aspire.aspireproject.dao.request.SignUpRequest;
import com.aspire.aspireproject.dao.response.JwtAuthenticationResponse;
import com.aspire.aspireproject.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/aspire/v1/auth")
public class AuthController {

    @Autowired
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String > signup(@RequestBody SignUpRequest request) {
        try{
            authService.signup(request);
            return ResponseEntity.ok("Successful");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(authService.signin(request));
    }
//    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@RequestBody Map<String, Object> requestBody){
//        try {
//            String username = (String) requestBody.get("email");
//            String password = (String) requestBody.get("password");
//            String firstName = (String) requestBody.get("firstName");
//            String lastName = (String) requestBody.get("lastName");
//            String role = (String) requestBody.get("role");
//            authService.(username, password, role, firstName, lastName);
//            return ResponseEntity.ok("User Registered Successfully");
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//
//    }

//    @PostMapping("/login")
//    public ResponseEntity<String> loginUser(@RequestBody Map<String, Object> requestBody){
//        String username = (String) requestBody.get("username");
//        String password = (String) requestBody.get("password");
//        try {
//            boolean isValidUser = authService.loginUser(username, password);
//            if(isValidUser) return ResponseEntity.ok("Login Successful");
//            return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST.getReasonPhrase());
//        }catch(Exception error){
//            return ResponseEntity.badRequest().body("Login Failed: "+error.getMessage());
//        }
//    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(String username){
        return ResponseEntity.ok("logged out successfully");
    }
}
