package com.aspire.aspireproject.model.loan;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ScheduledLoanRepayment {
    private Date date;
    private double remainingAmount;
    private LoanStatus status;
    private int termNo;
}
