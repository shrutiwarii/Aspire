package com.aspire.aspireproject.service;

import com.aspire.aspireproject.dao.request.LoanStatusRequest;
import com.aspire.aspireproject.dao.request.PaymentRequest;
import com.aspire.aspireproject.dao.request.TakeLoanRequest;
import com.aspire.aspireproject.dao.response.LoanStatusResponse;
import com.aspire.aspireproject.dao.response.PaymentResponse;
import com.aspire.aspireproject.dao.response.TakeLoanResponse;
import com.aspire.aspireproject.model.loan.Loan;
import com.aspire.aspireproject.model.loan.LoanStatus;
import com.aspire.aspireproject.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LoanService {

    TakeLoanResponse requestLoan(TakeLoanRequest request, String token);

    List<LoanStatusResponse> getMyLoans(String token, LoanStatus status);

    PaymentResponse payTermLoan(String token, PaymentRequest request);
     void approveLoan(String token, String loadId);

     LoanStatusResponse getLoanById(String token, String loanId);
}
