package com.aspire.aspireproject.dao.response;

import com.aspire.aspireproject.model.loan.LoanStatus;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class TakeLoanResponse {
    private String id;
    private LoanStatus status;
}
