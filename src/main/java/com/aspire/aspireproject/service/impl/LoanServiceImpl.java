package com.aspire.aspireproject.service.impl;

import com.aspire.aspireproject.dao.request.TakeLoanRequest;
import com.aspire.aspireproject.dao.response.TakeLoanResponse;
import com.aspire.aspireproject.model.loan.Loan;
import com.aspire.aspireproject.model.loan.LoanStatus;
import com.aspire.aspireproject.model.loan.ScheduledLoanRepayment;
import com.aspire.aspireproject.repository.LoanRepository;
import com.aspire.aspireproject.service.JwtService;
import com.aspire.aspireproject.service.LoanService;
import com.aspire.aspireproject.service.helper.LoanHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
    @Autowired
    private final LoanRepository loanRepository;

    private final JwtService jwtService;

    @Autowired
    private LoanHelper loanHelper;

    /**
     * Requests a loan with the provided information and returns the loan ID and status.
     *
     * @param request The loan request containing description, term, and amount.
     * @param token   The authentication token of the requester.
     * @return A {@link TakeLoanResponse} containing the loan ID and status.
     */
    @Override
    public TakeLoanResponse requestLoan(TakeLoanRequest request, String token) {
        Date date = new Date();
        List<ScheduledLoanRepayment> list = loanHelper.createRePayments(request.getAmount(), request.getTerm(), date);
        String username = jwtService.extractUserName(token);
        var loan = Loan.builder().username(username).status(LoanStatus.PENDING).description(request.getDescription())
                .term(request.getTerm()).amount(request.getAmount()).dateCreated(date).scheduledLoanRepayment(list).build();
        loanRepository.save(loan);
        TakeLoanResponse response = new TakeLoanResponse();
        response.setId(loan.getId());
        response.setStatus(loan.getStatus());
        return response;
    }

    @Override
    public boolean approveLoan(String loadId) {
        return false;
    }
}
