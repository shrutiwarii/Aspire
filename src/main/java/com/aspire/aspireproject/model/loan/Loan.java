package com.aspire.aspireproject.model.loan;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@Document(collection = "loans")
public class Loan {

    @Id
    private String id;
    private double amount;
    private int term;
    private String username;

    private String assignee;

    private LoanStatus status;

    private String description;
    private Date dateCreated;
    private List<ScheduledLoanRepayment> scheduledLoanRepayment;
}
