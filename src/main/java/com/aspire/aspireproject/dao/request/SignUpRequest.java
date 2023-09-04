package com.aspire.aspireproject.dao.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignUpRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String role;

}
