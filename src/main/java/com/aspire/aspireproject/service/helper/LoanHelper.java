package com.aspire.aspireproject.service.helper;
import com.aspire.aspireproject.dao.request.PaymentRequest;
import com.aspire.aspireproject.dao.response.LoanStatusResponse;
import com.aspire.aspireproject.model.loan.Loan;
import com.aspire.aspireproject.model.loan.LoanStatus;
import com.aspire.aspireproject.model.loan.ScheduledLoanRepayment;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class LoanHelper {
    public List<ScheduledLoanRepayment> createRePayments(double amount, int term, Date date){
        List<ScheduledLoanRepayment> list = new ArrayList<>();
        double repaymentAmount = amount / term;
        while(term>0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.WEEK_OF_YEAR, term);
            Date termDate = calendar.getTime();
            var scheduledLoan = ScheduledLoanRepayment.builder().remainingAmount(repaymentAmount).termNo(term).status(LoanStatus.PENDING).date(termDate).build();
            term--;
            list.add(scheduledLoan);
        }
        return list;
    }

    public  List<LoanStatusResponse> convertToLoanStatusResponse(List<Loan> loans) {
        return loans.stream()
                .map(loan -> LoanStatusResponse.builder().id(loan.getId()).description(loan.getDescription()).status(loan.getStatus())
                        .termsLeft(loan.getTerm())
                        .listOfRepayment(loan.getScheduledLoanRepayment()).build())
                .collect(Collectors.toList());
    }

    public void validatePaymentRequest(Loan loan, Double amount){

        // Check for valid loan id
        if(loan.getStatus().equals(LoanStatus.PAID)) throw new InvalidParameterException("Loan is already paid for this");

        //Check if more than required amount is getting paid
        if(loan.getAmountRemaining()<amount) throw new InvalidParameterException("You are paying more than required amount. Please check the entered value");

        //Check if the payment amount is more than the term payment amount
        if(loan.getScheduledLoanRepayment().stream()
            .anyMatch(payment -> Objects.equals(payment.getTermNo(), loan.getTerm()) && payment.getRemainingAmount() > loan.getAmount())) throw new InvalidParameterException("Please pay amount" +
                "equal to or more than the payment term amount. The payment term "+loan.getTerm()+" has amount "+loan.getAmount());

        //Check if the term number is valid and it is in PENDING state
        if(loan.getScheduledLoanRepayment().stream()
                .anyMatch(payment -> Objects.equals(payment.getTermNo(), loan.getTerm()) && payment.getStatus()==LoanStatus.PAID)) throw new InvalidParameterException("Either the term number does not exist or the payment is done already for this term");
    }

    public Loan updateLoan(Loan loan, PaymentRequest request){
        List<ScheduledLoanRepayment>  updatedLoan = loan.getScheduledLoanRepayment().stream().peek(rePayment -> {
            Integer termNo = rePayment.getTermNo();
            LoanStatus status = rePayment.getStatus();
            Double remainingAmount = rePayment.getRemainingAmount();

            if(Objects.equals(termNo, request.getTermNo()) && LoanStatus.PENDING.equals(status)){
                int newRemainingAmount = (int) (remainingAmount - request.getAmount());
                if(newRemainingAmount <=0){
                    loan.setAmountRemaining( (loan.getAmountRemaining()-rePayment.getRemainingAmount()));
                    rePayment.setRemainingAmount(0.0);
                    rePayment.setStatus(LoanStatus.PAID);
                }
            }
        }).toList();
        loan.setScheduledLoanRepayment(updatedLoan);

        //Check if all the statuses are PAID for scheduled re payments
        boolean allPaid = loan.getScheduledLoanRepayment().stream()
                .allMatch(repayment -> LoanStatus.PAID.equals(repayment.getStatus()));

        if(allPaid){
            loan.setStatus(LoanStatus.PAID);
        }
        return loan;
    }

}
