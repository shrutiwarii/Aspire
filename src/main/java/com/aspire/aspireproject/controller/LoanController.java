package com.aspire.aspireproject.controller;

import com.aspire.aspireproject.dao.request.PaymentRequest;
import com.aspire.aspireproject.dao.request.TakeLoanRequest;
import com.aspire.aspireproject.dao.response.LoanStatusResponse;
import com.aspire.aspireproject.dao.response.PaymentResponse;
import com.aspire.aspireproject.dao.response.TakeLoanResponse;
import com.aspire.aspireproject.model.loan.LoanStatus;
import com.aspire.aspireproject.service.LoanService;
import com.aspire.aspireproject.service.mock.MockPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/aspire/v1/project")
public class LoanController {
    @Autowired
    private LoanService loanService;
    @PostMapping("/requestLoan")
    public ResponseEntity<TakeLoanResponse> requestLoan(@RequestHeader("Authorization") String token, @RequestBody TakeLoanRequest request){
        try {
            TakeLoanResponse response = loanService.requestLoan(request,token.split(" ")[1]);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }

    }


    @GetMapping("/getLoans")
    public ResponseEntity<List<LoanStatusResponse>> getMyLoans(@RequestHeader("Authorization") String token, @RequestParam(required = false) LoanStatus status){
        List<LoanStatusResponse> response = loanService.getMyLoans(token.split(" ")[1], status);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/payLoan")
    public ResponseEntity<?> payTermLoan(@RequestHeader("Authorization") String token, @RequestBody PaymentRequest paymentRequest){
        try {
            PaymentResponse response = loanService.payTermLoan(token.split(" ")[1], paymentRequest);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/approveLoan")
    public ResponseEntity<?> approveLoan(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> requestBody){
        try {
            String loanId = requestBody.get("loanId");
            loanService.approveLoan(token.split(" ")[1], loanId);
            return ResponseEntity.ok("Yay loan approved successfully. Load ID: "+loanId);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Failed to approve the loan: "+e);
        }
    }

    @GetMapping("/getLoanById")
    public ResponseEntity<String> getLoanById( String loanId){
        return ResponseEntity.ok("Successful and your loan id is: Currently your loan is in PENDING state. You will get a notification when it moves to APPROVED");

    }

    @PostMapping("/approveLoanById")
    public ResponseEntity<String> approveLoanById(){
        return ResponseEntity.ok("Successful and your loan id is: Currently your loan is in PENDING state. You will get a notification when it moves to APPROVED");

    }
}
