package com.aspire.aspireproject.service;

import com.aspire.aspireproject.dao.request.TakeLoanRequest;
import com.aspire.aspireproject.dao.response.TakeLoanResponse;
import com.aspire.aspireproject.model.loan.Loan;
import com.aspire.aspireproject.model.loan.LoanStatus;
import com.aspire.aspireproject.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface LoanService {

    TakeLoanResponse requestLoan(TakeLoanRequest request, String token);
     boolean approveLoan(String loadId);
}
