package com.aspire.aspireproject.service.helper;

import com.aspire.aspireproject.dao.request.SignUpRequest;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.util.Objects;

import static com.aspire.aspireproject.model.user.Role.ADMIN;
import static com.aspire.aspireproject.model.user.Role.CUSTOMER;

@Component
public class AuthHelper {
    public String extractEmailDomain(String email) {
        if (email != null && email.contains("@")) {
            String[] parts = email.split("@");
            if (parts.length == 2) {
                return parts[1];
            }
        }
        return null;
    }

    public boolean isBankEmployee(String username){
        String domain = extractEmailDomain(username);
        if (domain==null) throw new InvalidParameterException("Invalid username. Please provide a valid email as your username");
        return Objects.equals(domain, "aspire.com");
    }

    public void validateSignupParams(SignUpRequest request){
        if(request.getUsername().isEmpty() || request.getPassword().isEmpty() || request.getRole().isEmpty() || request.getFirstName().isEmpty() || request.getLastName().isEmpty())
            throw new InvalidParameterException("Required fields missing");

        String role = request.getRole().toLowerCase();
        String username = request.getUsername();

        if (ADMIN.getDisplayName().equals(role)) {
            if (!isBankEmployee(username)) {
                throw new InvalidParameterException("You cannot register as an admin because you don't have enough permissions.");
            }
        }

        if (CUSTOMER.getDisplayName().equals(role)) {
            if (isBankEmployee(username)) {
                throw new InvalidParameterException("Please use your personal ID to register as a customer and not your corporate ID.");
            }
        }
    }

}
