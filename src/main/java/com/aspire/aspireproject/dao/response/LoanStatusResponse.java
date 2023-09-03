package com.aspire.aspireproject.dao.response;

import com.aspire.aspireproject.model.loan.LoanStatus;
import com.aspire.aspireproject.model.loan.ScheduledLoanRepayment;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoanStatusResponse {
    private String id;
    private String description;
    private LoanStatus status;
    private Integer termsLeft;
    private List<ScheduledLoanRepayment> listOfRepayment;
}
