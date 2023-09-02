package com.aspire.aspireproject.dao.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TakeLoanRequest {
    private double amount;
    private int term;
    private String username;
    private String description;
}
