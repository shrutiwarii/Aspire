package com.aspire.aspireproject.service.impl;

import com.aspire.aspireproject.dao.request.PaymentRequest;
import com.aspire.aspireproject.dao.request.TakeLoanRequest;
import com.aspire.aspireproject.dao.response.LoanStatusResponse;
import com.aspire.aspireproject.dao.response.PaymentResponse;
import com.aspire.aspireproject.dao.response.TakeLoanResponse;
import com.aspire.aspireproject.exception.InvalidLoanIdException;
import com.aspire.aspireproject.model.loan.Loan;
import com.aspire.aspireproject.model.loan.LoanStatus;
import com.aspire.aspireproject.model.loan.ScheduledLoanRepayment;
import com.aspire.aspireproject.model.user.Role;
import com.aspire.aspireproject.model.user.User;
import com.aspire.aspireproject.repository.LoanRepository;
import com.aspire.aspireproject.repository.UserRepository;
import com.aspire.aspireproject.service.JwtService;
import com.aspire.aspireproject.service.LoanService;
import com.aspire.aspireproject.service.helper.LoanHelper;
import com.aspire.aspireproject.service.mock.MockPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    @Autowired
    public LoanHelper loanHelper;

    @Autowired
    public MockPaymentService mockPaymentService;


    /**
     * Requests a loan with the provided information and returns the loan ID and status.
     *
     * @param request The loan request containing description, term, and amount.
     * @param token   The authentication token of the requester.
     * @return A {@link TakeLoanResponse} containing the loan ID and status.
     */
    @Override
    public TakeLoanResponse requestLoan(TakeLoanRequest request, String token) {
        String username = jwtService.extractUserName(token);
        loanHelper.validRequestLoan(request);
        Date date = new Date();
        List<ScheduledLoanRepayment> list = loanHelper.createRePayments(request.getAmount(), request.getTerm(), date);
        Loan loan = Loan.builder().username(username)
                                .status(LoanStatus.PENDING)
                                .description(request.getDescription())
                                .term(request.getTerm())
                                .amount(request.getAmount())
                                .dateCreated(date)
                                .scheduledLoanRepayment(list)
                                .termsLeft(request.getTerm())
                                .amountRemaining(request.getAmount())
                                .build();
        loanRepository.save(loan);
        TakeLoanResponse response = new TakeLoanResponse();
        response.setId(loan.getId());
        response.setStatus(loan.getStatus());
        return response;
    }

    /**
     * Retrieves a list of loan statuses associated with the authenticated user.
     *
     * @param token The authentication token of the user.
     * @return A list of {@link LoanStatusResponse} objects representing the loan statuses.
     * @throws UsernameNotFoundException If the provided authentication token is invalid or the user is not found.
     */
    @Override
    public List<LoanStatusResponse> getMyLoans( String token, LoanStatus status) {
        String username = jwtService.extractUserName(token);
        List<Loan> myLoans;
        if (status==null) {
            myLoans = loanRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Invalid email or username"));
        } else {
            myLoans = loanRepository.findByUsernameAndStatus(username,status).orElseThrow(()-> new UsernameNotFoundException("Invalid email or username"));
        }
        return loanHelper.convertToLoanStatusResponse(myLoans);
    }

    /**
     * Processes a payment for a specific term of a loan.
     * This method validates the provided payment request, simulates payment processing,
     * and updates the loan's status if the payment is successful.
     *
     * @param token The authentication token for the payment request.
     * @param request The payment request containing the amount, term number, source, and loan ID.
     * @return A PaymentResponse object representing the result of the payment operation.
     * @throws InvalidParameterException If the payment request is missing required fields
     *         or if the payment fails for any reason.
     * @throws InvalidLoanIdException If the provided loan ID is not valid or does not exist.
     */
    @Override
    public PaymentResponse payTermLoan(String token, PaymentRequest request) {
        String username = jwtService.extractUserName(token);
        Loan loan = loanRepository.findById(request.getId()).orElseThrow(()->new InvalidLoanIdException("Loan id is invalid"));

        //Check if user is the owner of the loan
        if(!Objects.equals(loan.getUsername(), username)) throw new InvalidParameterException("You cannot pay this loan as you are not the owner of this loan");

        if(request.getAmount()==0 || request.getTermNo()==0 || request.getSource()==null || request.getId()==null) {
            throw new InvalidParameterException("Please provide the required fields");
        }

        loanHelper.validatePaymentRequest(loan, request);
        Loan updatedLoan;
        PaymentResponse paymentResponse = new PaymentResponse();

        //check if the term date has passed then charge as per 1% interest per day and add it to the total amount to be paid and inform the customer in the response message
        if(loanHelper.isPreviousTermPayment(loan, request)) {
            updatedLoan = loanHelper.previousTermLoanUpdate(loan, request);
            paymentResponse.setMessage("Updated a previous term with 1% interest");
        }else{
            updatedLoan = loanHelper.updateLoan(loan,request);
            paymentResponse.setMessage("Returning the excess amount");
        }

        // Simulate payment processing.
        boolean paymentSuccessful = mockPaymentService.processPayment(request.getAmount(), request.getSource(), request.getTermNo());
        if (paymentSuccessful){

            loanRepository.save(updatedLoan);
            paymentResponse.setTermNo(request.getTermNo());
            paymentResponse.setStatus(LoanStatus.PAID);

            return paymentResponse;
        }

        throw new InvalidParameterException("Payment failed");
    }

    /**
     * Approves or rejects a loan application based on the provided token and loan ID.
     * This method validates the user's permissions and the loan's status before approving or rejecting the loan.
     *
     * @param token The authentication token of the user performing the approval.
     * @param loanId The unique identifier of the loan application to be approved or rejected.
     * @throws InvalidLoanIdException If the provided loan ID is not valid or does not exist.
     * @throws UsernameNotFoundException If the user specified by the token is not found or has an invalid role.
     * @throws InvalidParameterException If the user does not have sufficient permissions, or if the loan is not in a PENDING state.
     */
    @Override
    public void approveLoan(String token, String loanId) {
        String username = jwtService.extractUserName(token);
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new InvalidLoanIdException("Invalid loan Id"));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Invalid email or username"));
        if (Role.ADMIN != user.getRole() || Objects.equals(user.getUsername(), loan.getUsername())) {
            throw new InvalidParameterException("You don't have enough permissions");
        }
        if (!LoanStatus.PENDING.equals(loan.getStatus()))
            throw new InvalidParameterException("Loan is not in PENDING state. Can't approve");

        loan.setApprover(username);
        //Set conditions for rejecting the loan
        if(loan.getAmount()>1000000){
            loan.setStatus(LoanStatus.REJECTED);
        }else {
            loan.setStatus(LoanStatus.APPROVED);
        }
        loanRepository.save(loan);
    }

    /**
     * Retrieve the details of a loan by its unique identifier.
     * This method fetches the loan information based on the provided loan ID and token for authentication.
     * It checks if the requester is the owner of the loan or has the necessary permissions to access the details.
     *
     * @param token The authentication token of the user making the request.
     * @param loanId The unique identifier of the loan to retrieve.
     * @return A LoanStatusResponse object containing the loan's status, repayment schedule, description, and remaining terms.
     * @throws InvalidLoanIdException If the provided loan ID is not valid or does not exist.
     * @throws InvalidParameterException If the requester is not the owner of the loan or lacks the required permissions to access the details.
     */
    @Override
    public LoanStatusResponse getLoanById(String token, String loanId) {
        String username = jwtService.extractUserName(token);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Invalid email or username"));
        Loan loan = loanRepository.findById(loanId).orElseThrow(()-> new InvalidLoanIdException("Invalid loan Id"));
        if (!Role.ADMIN.equals(user.getRole())&&!username.equals(loan.getUsername())) throw new InvalidParameterException("You are not the owner of this loan Id so you can't see the details");
        return LoanStatusResponse.builder()
                .id(loanId).status(loan.getStatus())
                .listOfRepayment(loan.getScheduledLoanRepayment())
                .description(loan.getDescription())
                .termsLeft(loan.getTermsLeft())
                .username(username)
                .build();
    }
}
