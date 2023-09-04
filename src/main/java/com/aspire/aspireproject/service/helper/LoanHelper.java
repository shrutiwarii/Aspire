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
            ScheduledLoanRepayment scheduledLoan = ScheduledLoanRepayment.builder().remainingAmount(repaymentAmount).termNo(term).status(LoanStatus.PENDING).date(termDate).build();
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

    //TODO: create new enum for term payments, loanStatus -> repaymentStatus
    public void validatePaymentRequest(Loan loan, PaymentRequest request){

        //Check if the term number is valid and it is in PENDING state
        if(loan.getScheduledLoanRepayment().stream()
                .anyMatch(payment -> Objects.equals(payment.getTermNo(), request.getTermNo()) && payment.getStatus()==LoanStatus.PAID))
            throw new InvalidParameterException("Either the term number does not exist or the payment is done already for this term");

        // Check for valid loan id
        if(loan.getStatus().equals(LoanStatus.PAID)) throw new InvalidParameterException("Loan is already paid for this");

        //Check if the  loan is not approved
        if(!loan.getStatus().equals(LoanStatus.APPROVED) ) throw new InvalidParameterException("Loan is not approved");

        //Check if more than required amount is getting paid
        if(loan.getAmountRemaining()<request.getAmount()) throw new InvalidParameterException("You are paying more than total remaining amount. Please check the entered value. Remaining amount: $"+loan.getAmountRemaining());

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
                    Integer termsLeft =loan.getTermsLeft()-1;
                    loan.setTermsLeft(termsLeft);
                    rePayment.setRemainingAmount(0.0);
                    rePayment.setStatus(LoanStatus.PAID);
                    rePayment.setPaymentDate(new Date());
                }
            }
        }).toList();
        if (loan.getAmountRemaining()<1) loan.setAmountRemaining(0.0);
        loan.setScheduledLoanRepayment(updatedLoan);

        //Check if all the statuses are PAID for scheduled re payments
        boolean allPaid = loan.getScheduledLoanRepayment().stream()
                .allMatch(repayment -> LoanStatus.PAID.equals(repayment.getStatus()));

        if(allPaid){
            loan.setStatus(LoanStatus.PAID);
        }
        return loan;
    }

    public boolean isPreviousTermPayment(Loan loan, PaymentRequest request){
        ScheduledLoanRepayment scheduledLoanRepayment = loan.getScheduledLoanRepayment().stream().filter(repayment -> repayment.getTermNo() == request.getTermNo()).findFirst().orElse(null);
        if(scheduledLoanRepayment!=null) {
            Date termDate = scheduledLoanRepayment.getDate();
            Date currentDate = new Date();
            return currentDate.after(termDate);
        }
        throw new InvalidParameterException("term not found");
    }

    //TODO: add newAmountAfterInterest field
    public Loan previousTermLoanUpdate(Loan loan, PaymentRequest request){
        ScheduledLoanRepayment scheduledLoanRepayment = loan.getScheduledLoanRepayment().stream().filter(repayment -> repayment.getTermNo() == request.getTermNo()).findFirst().orElse(null);
        Date termDate = scheduledLoanRepayment.getDate();
        Date currentDate = new Date();
        // Previous terms are not paid, apply interest
        double interestRate = 0.01; // 1% interest rate per day
        long daysLate = (currentDate.getTime() - termDate.getTime()) / (1000 * 60 * 60 * 24);
        double interestAmount = scheduledLoanRepayment.getRemainingAmount() * interestRate * daysLate;

        if(request.getAmount()>interestAmount+scheduledLoanRepayment.getRemainingAmount()) {
            loan.setAmount(loan.getAmount()+interestAmount);
            loan.setAmountRemaining(loan.getAmountRemaining()-scheduledLoanRepayment.getRemainingAmount());
            scheduledLoanRepayment.setRemainingAmount(0.0);
            scheduledLoanRepayment.setStatus(LoanStatus.PAID);
            Integer termsLeft =loan.getTermsLeft()-1;
            loan.setTermsLeft(termsLeft);
            scheduledLoanRepayment.setPaymentDate(currentDate);
            if (loan.getAmountRemaining()<1) loan.setAmountRemaining(0.0);
        }
        else {
            throw new InvalidParameterException("Not enough funds");
        }
        //Check if all the statuses are PAID for scheduled re payments
        boolean allPaid = loan.getScheduledLoanRepayment().stream()
                .allMatch(repayment -> LoanStatus.PAID.equals(repayment.getStatus()));

        if(allPaid){
            loan.setStatus(LoanStatus.PAID);
        }
        loan.getScheduledLoanRepayment().set(loan.getScheduledLoanRepayment().indexOf(scheduledLoanRepayment), scheduledLoanRepayment);
        return loan;
    }

}
