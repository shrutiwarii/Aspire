package com.aspire.aspireproject.service.helper;

import com.aspire.aspireproject.dao.request.SignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.*;


public class AuthHelperTest {

    private AuthHelper authHelper;

    @BeforeEach
    public void setUp() {
        authHelper = new AuthHelper();
    }

    @Test
    public void testExtractEmailDomain() {
        // Test when a valid email is provided
        String email = "user@example.com";
        String domain = authHelper.extractEmailDomain(email);
        assertEquals("example.com", domain);

        // Test when an invalid email is provided
        email = "invalid_email";
        domain = authHelper.extractEmailDomain(email);
        assertNull(domain);
    }

    @Test
    public void testIsBankEmployee() {
        // Test when the username is a bank employee's email
        String username = "employee@aspire.com";
        assertTrue(authHelper.isBankEmployee(username));

        // Test when the username is not a bank employee's email
        username = "user@example.com";
        assertFalse(authHelper.isBankEmployee(username));
    }

    @Test
    public void testValidateSignupParams() {
        // Test valid customer registration
        SignUpRequest customerRequest = new SignUpRequest("abc", "pqr", "user@example.com", "abc", "CUSTOMER");
        assertDoesNotThrow(() -> authHelper.validateSignupParams(customerRequest));

        // Test valid admin registration
        SignUpRequest adminRequest = new SignUpRequest("abc", "pqr", "user@aspire.com", "abc", "ADMIN");
        assertDoesNotThrow(() -> authHelper.validateSignupParams(adminRequest));

        // Test missing required fields
        SignUpRequest missingFieldsRequest = new SignUpRequest("", "", "", "", "");
        assertThrows(InvalidParameterException.class, () -> authHelper.validateSignupParams(missingFieldsRequest));

        // Test customer registration with a corporate email
        SignUpRequest invalidCustomerRequest = new SignUpRequest("abc", "pqr", "user@aspire.com", "abc", "CUSTOMER");
        assertThrows(InvalidParameterException.class, () -> authHelper.validateSignupParams(invalidCustomerRequest));

        // Test admin registration with a personal email
        SignUpRequest invalidAdminRequest = new SignUpRequest("abc", "pqr", "user@example.com", "abc", "ADMIN");
        assertThrows(InvalidParameterException.class, () -> authHelper.validateSignupParams(invalidAdminRequest));
    }
}

