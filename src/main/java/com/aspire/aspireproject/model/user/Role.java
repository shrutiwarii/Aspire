package com.aspire.aspireproject.model.user;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("admin"),
    CUSTOMER("customer");

    private final String displayName;
    Role(String displayName) {
        this.displayName = displayName;
    }

}
