package com.aspire.aspireproject.service.impl;

import com.aspire.aspireproject.dao.request.TakeLoanRequest;
import com.aspire.aspireproject.dao.response.LoanStatusResponse;
import com.aspire.aspireproject.dao.response.TakeLoanResponse;
import com.aspire.aspireproject.model.loan.Loan;
import com.aspire.aspireproject.model.loan.LoanStatus;
import com.aspire.aspireproject.model.loan.ScheduledLoanRepayment;
import com.aspire.aspireproject.repository.LoanRepository;
import com.aspire.aspireproject.repository.UserRepository;
import com.aspire.aspireproject.service.JwtService;
import com.aspire.aspireproject.service.helper.LoanHelper;
import com.aspire.aspireproject.service.mock.MockPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanServiceImplTest {

    private LoanServiceImpl loanService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private LoanHelper loanHelper;

    @Mock
    private MockPaymentService mockPaymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        loanService = new LoanServiceImpl(loanRepository, userRepository, jwtService);
        loanService.loanHelper = loanHelper;
        loanService.mockPaymentService = mockPaymentService;
    }

    @Test
    void testRequestLoan() {
        String token = "validToken";
        TakeLoanRequest request = new TakeLoanRequest();
        request.setDescription("Test loan");
        request.setAmount(1000.0);
        request.setTerm(5);

        String username = "testuser";
        when(jwtService.extractUserName(token)).thenReturn(username);

        List<ScheduledLoanRepayment> repayments = new ArrayList<>();
        // Add logic to mock the creation of repayments in loanHelper

        Loan loan = Loan.builder()
                .username(username)
                .status(LoanStatus.PENDING)
                .description(request.getDescription())
                .term(request.getTerm())
                .amount(request.getAmount())
                .scheduledLoanRepayment(repayments)
                .termsLeft(request.getTerm())
                .amountRemaining(request.getAmount())
                .build();

        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        TakeLoanResponse response = loanService.requestLoan(request, token);

        assertNotNull(response);
        assertEquals(loan.getId(), response.getId());
        assertEquals(loan.getStatus(), response.getStatus());
    }

    @Test
    void testGetMyLoans() {
        String token = "validToken";
        String username = "testuser";
        LoanStatus status = LoanStatus.PENDING;

        when(jwtService.extractUserName(token)).thenReturn(username);

        List<Loan> loans = new ArrayList<>();
        // Add logic to mock the retrieval of loans from the repository

        when(loanRepository.findByUsernameAndStatus(username, status)).thenReturn(Optional.of(loans));

        List<LoanStatusResponse> responses = loanService.getMyLoans(token, status);

        assertNotNull(responses);
    }

}
