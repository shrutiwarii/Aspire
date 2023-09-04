package com.aspire.aspireproject.controller;

import com.aspire.aspireproject.dao.request.TakeLoanRequest;
import com.aspire.aspireproject.dao.request.PaymentRequest;
import com.aspire.aspireproject.dao.response.TakeLoanResponse;
import com.aspire.aspireproject.dao.response.PaymentResponse;
import com.aspire.aspireproject.dao.response.LoanStatusResponse;
import com.aspire.aspireproject.model.loan.LoanStatus;
import com.aspire.aspireproject.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

public class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRequestLoan() {
        // Mock data
        TakeLoanRequest request = new TakeLoanRequest();
        String token = "your_token";
        TakeLoanResponse response = new TakeLoanResponse(); // Customize this as needed

        // Mock service method
        when(loanService.requestLoan(request, token)).thenReturn(response);

        // Test the controller method
        ResponseEntity<?> result = loanController.requestLoan(token, request);

        // Verify the service method was called and check the response
        verify(loanService, times(1)).requestLoan(request, token);
        assertSame(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    public void testGetMyLoans() {
        // Mock data
        String token = "your_token";
        LoanStatus status = LoanStatus.APPROVED; // Change this as needed
        List<LoanStatusResponse> responseList = new ArrayList<>(); // Customize this as needed

        // Mock service method
        when(loanService.getMyLoans(token, status)).thenReturn(responseList);

        // Test the controller method
        ResponseEntity<List<LoanStatusResponse>> result = loanController.getMyLoans(token, status);

        // Verify the service method was called and check the response
        verify(loanService, times(1)).getMyLoans(token, status);
        assertSame(HttpStatus.OK, result.getStatusCode());
        assertSame(responseList, result.getBody());
    }

    @Test
    public void testPayTermLoan() {
        // Mock data
        String token = "your_token";
        PaymentRequest paymentRequest = new PaymentRequest(); // Customize this as needed
        PaymentResponse response = new PaymentResponse(); // Customize this as needed

        // Mock service method
        when(loanService.payTermLoan(token, paymentRequest)).thenReturn(response);

        // Test the controller method
        ResponseEntity<?> result = loanController.payTermLoan(token, paymentRequest);

        // Verify the service method was called and check the response
        verify(loanService, times(1)).payTermLoan(token, paymentRequest);
        assertSame(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    public void testApproveLoan() {
        // Mock data
        String token = "your_token";
        String loanId = "your_loan_id";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("loanId", loanId);

        // Test the controller method
        ResponseEntity<?> result = loanController.approveLoan(token, requestBody);

        // Verify the service method was called and check the response
        verify(loanService, times(1)).approveLoan(token, loanId);
        assertSame(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testGetLoanById() {
        // Mock data
        String token = "your_token";
        String loanId = "your_loan_id";
        LoanStatusResponse response = LoanStatusResponse.builder().build(); // Customize this as needed

        // Mock service method
        when(loanService.getLoanById(token, loanId)).thenReturn(response);

        // Test the controller method
        ResponseEntity<?> result = loanController.getLoanById(token, loanId);

        // Verify the service method was called and check the response
        verify(loanService, times(1)).getLoanById(token, loanId);
        assertSame(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }
}

