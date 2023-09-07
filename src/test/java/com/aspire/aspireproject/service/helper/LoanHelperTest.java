package com.aspire.aspireproject.service.helper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;

import com.aspire.aspireproject.dao.request.PaymentRequest;
import com.aspire.aspireproject.dao.request.TakeLoanRequest;
import com.aspire.aspireproject.dao.response.LoanStatusResponse;
import com.aspire.aspireproject.model.loan.Loan;
import com.aspire.aspireproject.model.loan.LoanStatus;
import com.aspire.aspireproject.model.loan.ScheduledLoanRepayment;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Calendar;

@SpringBootTest
class LoanHelperTest {

    private LoanHelper loanHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        loanHelper = new LoanHelper();
    }

    @Test
    void testValidRequestLoanValidInput() {
        // Valid input
        TakeLoanRequest request = new TakeLoanRequest();
        request.setAmount(1000.0);
        request.setTerm(12);
        assertDoesNotThrow(() -> loanHelper.validateRequestLoan(request));
    }

    @Test
    void testValidRequestLoanInvalidInput() {
        // Invalid input, both amount and term are zero
        TakeLoanRequest request = new TakeLoanRequest();
        request.setAmount(0.0);
        request.setTerm(0);
        assertThrows(InvalidParameterException.class, () -> loanHelper.validateRequestLoan(request));
    }

    @Test
    void testCreateRePayments() {
        double amount = 1000.0;
        int term = 12;
        Date date = new Date();
        List<ScheduledLoanRepayment> repayments = loanHelper.createRePayments(amount, term, date);

        assertNotNull(repayments);
        assertEquals(term, repayments.size());

        double totalRepaymentAmount = repayments.stream()
                .mapToDouble(ScheduledLoanRepayment::getRemainingAmount)
                .sum();
        assertEquals(amount, totalRepaymentAmount);

        // Check the repayment dates
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, term);
        Date lastRepaymentDate = calendar.getTime();
        assertEquals(lastRepaymentDate, repayments.get(0).getDate());
    }

    @Test
    void testConvertToLoanStatusResponse() {
        Loan loan1 = Loan.builder().build();
        loan1.setId("1L");
        loan1.setDescription("Loan 1");
        loan1.setStatus(LoanStatus.APPROVED);
        loan1.setTerm(12);

        Loan loan2 = Loan.builder().build();
        loan2.setId("2L");
        loan2.setDescription("Loan 2");
        loan2.setStatus(LoanStatus.PENDING);
        loan2.setTerm(6);

        List<LoanStatusResponse> response = loanHelper.convertToLoanStatusResponse(Arrays.asList(loan1, loan2));

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(loan1.getId(), response.get(0).getId());
        assertEquals(loan1.getDescription(), response.get(0).getDescription());
        assertEquals(loan1.getStatus(), response.get(0).getStatus());
        assertEquals(loan1.getTerm(), response.get(0).getTermsLeft());

        assertEquals(loan2.getId(), response.get(1).getId());
        assertEquals(loan2.getDescription(), response.get(1).getDescription());
        assertEquals(loan2.getStatus(), response.get(1).getStatus());
        assertEquals(loan2.getTerm(), response.get(1).getTermsLeft());
    }

    @Test
    void testValidatePaymentRequestValid() {
        Loan loan = Loan.builder().build();
        loan.setStatus(LoanStatus.APPROVED);
        loan.setAmountRemaining(1000.0);
        ScheduledLoanRepayment repayment = ScheduledLoanRepayment.builder().build();
        repayment.setTermNo(1);
        repayment.setStatus(LoanStatus.PENDING);
        loan.setScheduledLoanRepayment(Arrays.asList(repayment));

        PaymentRequest request = new PaymentRequest();
        request.setTermNo(1);
        request.setAmount(500.0);

        assertDoesNotThrow(() -> loanHelper.validatePaymentRequest(loan, request));
    }

    @Test
    void testValidatePaymentRequestAlreadyPaid() {
        Loan loan = Loan.builder().build();
        loan.setStatus(LoanStatus.PAID); // Loan is already paid
        loan.setAmountRemaining(0.0);
        ScheduledLoanRepayment repayment = ScheduledLoanRepayment.builder().build();
        repayment.setTermNo(1);
        repayment.setStatus(LoanStatus.PENDING);
        loan.setScheduledLoanRepayment(Arrays.asList(repayment));

        PaymentRequest request = new PaymentRequest();
        request.setTermNo(1);
        request.setAmount(500.0);

        assertThrows(InvalidParameterException.class, () -> loanHelper.validatePaymentRequest(loan, request));
    }

    @Test
    void testAreAllTermsPaid() {
        Loan loan = Loan.builder().build();
        ScheduledLoanRepayment repayment1 = ScheduledLoanRepayment.builder().build();
        repayment1.setStatus(LoanStatus.PAID);
        ScheduledLoanRepayment repayment2 = ScheduledLoanRepayment.builder().build();
        repayment2.setStatus(LoanStatus.PAID);
        loan.setScheduledLoanRepayment(Arrays.asList(repayment1, repayment2));

        assertTrue(loanHelper.areAllTermsPaid(loan));
    }


}
