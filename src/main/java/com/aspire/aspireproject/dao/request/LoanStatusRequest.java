package com.aspire.aspireproject.dao.request;

import com.aspire.aspireproject.model.loan.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanStatusRequest {
    private LoanStatus status;
}
