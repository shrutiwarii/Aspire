package com.aspire.aspireproject.dao.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TakeLoanRequest {
    private Double amount;
    private Integer term;
    private String description;
}
