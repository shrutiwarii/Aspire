package com.aspire.aspireproject.dao.response;

import com.aspire.aspireproject.model.loan.LoanStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentResponse {
    private int termNo;
    private LoanStatus status;
    private String message;
}
