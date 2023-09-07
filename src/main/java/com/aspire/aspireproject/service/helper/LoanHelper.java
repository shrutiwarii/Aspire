package com.aspire.aspireproject.service.helper;
import com.aspire.aspireproject.dao.request.PaymentRequest;
import com.aspire.aspireproject.dao.request.TakeLoanRequest;
import com.aspire.aspireproject.dao.response.LoanStatusResponse;
import com.aspire.aspireproject.model.loan.Loan;
import com.aspire.aspireproject.model.loan.LoanStatus;
import com.aspire.aspireproject.model.loan.ScheduledLoanRepayment;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class LoanHelper {

    public String generateToken(TakeLoanRequest request, String username){
        String inputData = username + request.getAmount() + request.getTerm();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(inputData.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }

    }
    public void validateRequestLoan(TakeLoanRequest request){
        if(request.getAmount()==null || request.getTerm()==null || request.getTerm()<=0 || request.getAmount()<=0 )
            throw new InvalidParameterException("Please provide correct values for required fields: AMOUNT and TERM");
    }

    /**
     * Generates a list of scheduled loan repayments based on the provided parameters.
     *
     * @param amount The total loan amount.
     * @param term The loan term in weeks.
     * @param date The start date of the loan.
     * @return A list of {@link ScheduledLoanRepayment} objects representing scheduled loan repayments.
     */

    public List<ScheduledLoanRepayment> createRePayments(double amount, int term, Date date){
        List<ScheduledLoanRepayment> repayments = new ArrayList<>();
        double repaymentAmount = amount / term;
        while(term>0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.WEEK_OF_YEAR, term);
            Date termDate = calendar.getTime();
            ScheduledLoanRepayment scheduledLoan = ScheduledLoanRepayment
                    .builder()
                    .remainingAmount(repaymentAmount)
                    .termNo(term)
                    .status(LoanStatus.PENDING)
                    .date(termDate).build();
            term--;
            repayments.add(scheduledLoan);
        }
        return repayments;
    }

    /**
     * Converts a list of {@link Loan} objects into a list of {@link LoanStatusResponse} objects.
     *
     * @param loans A list of {@link Loan} objects to be converted.
     * @return A list of {@link LoanStatusResponse} objects containing loan status information.
     */

    public  List<LoanStatusResponse> convertToLoanStatusResponse(List<Loan> loans) {
        return loans.stream()
                .map(loan -> LoanStatusResponse.builder().id(loan.getId()).description(loan.getDescription()).status(loan.getStatus())
                        .termsLeft(loan.getTerm())
                        .listOfRepayment(loan.getScheduledLoanRepayment()).build())
                .collect(Collectors.toList());
    }

    /**
     * Validates a payment request for a loan to ensure it meets the necessary criteria.
     *
     * @param loan The {@link Loan} for which the payment is being validated.
     * @param request The {@link PaymentRequest} containing payment details.
     *
     */

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

    /**
     * Updates a loan based on a payment request, marking the corresponding repayment as paid
     * and adjusting loan properties as necessary.
     *
     * @param loan The {@link Loan} to update.
     * @param request The {@link PaymentRequest} for the payment.
     * @return The updated {@link Loan} object.
     */

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


        if(areAllTermsPaid(loan)){
            loan.setStatus(LoanStatus.PAID);
        }
        return loan;
    }

    public boolean areAllTermsPaid(Loan loan){
        return loan.getScheduledLoanRepayment().stream()
                .allMatch(repayment -> LoanStatus.PAID.equals(repayment.getStatus()));
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


    /**
     * Updates a loan for a previous term payment, applying interest and marking the corresponding repayment as paid.
     *
     * @param loan The {@link Loan} to update.
     * @param request The {@link PaymentRequest} for the payment.
     * @return The updated {@link Loan} object.
     * @throws InvalidParameterException If the payment amount is insufficient to cover interest and remaining amount.
     */
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

        if(areAllTermsPaid(loan)){
            loan.setStatus(LoanStatus.PAID);
        }
        loan.getScheduledLoanRepayment().set(loan.getScheduledLoanRepayment().indexOf(scheduledLoanRepayment), scheduledLoanRepayment);
        return loan;
    }

}
