package com.aspire.aspireproject.service.helper;

import com.aspire.aspireproject.dao.request.SignUpRequest;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public boolean isValidEmail(String email){
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public void validateSignupParams(SignUpRequest request){
        if(request.getUsername().isEmpty() || request.getPassword().isEmpty() || request.getRole().isEmpty() || request.getFirstName().isEmpty() || request.getLastName().isEmpty())
            throw new InvalidParameterException("Required fields missing");

        if (!isValidEmail(request.getUsername()))
            throw new InvalidParameterException("Not correct email");

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
