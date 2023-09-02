package com.aspire.aspireproject.dao.request;

import lombok.Getter;

@Getter
public class SignUpRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String password;

    private String role;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
