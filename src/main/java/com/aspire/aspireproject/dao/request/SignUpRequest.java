package com.aspire.aspireproject.dao.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class SignUpRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String password;

    private String role;

    public void setUsername(String username) {
        this.username = username;
    }

}
