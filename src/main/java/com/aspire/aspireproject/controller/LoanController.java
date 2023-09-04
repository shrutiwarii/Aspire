package com.aspire.aspireproject.controller;

import com.aspire.aspireproject.dao.request.PaymentRequest;
import com.aspire.aspireproject.dao.request.TakeLoanRequest;
import com.aspire.aspireproject.dao.response.*;
import com.aspire.aspireproject.model.loan.LoanStatus;
import com.aspire.aspireproject.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/aspire/v1/project")
public class LoanController {
    @Autowired
    private LoanService loanService;

    @PostMapping("/requestLoan")
    public ResponseEntity<?> requestLoan(@RequestHeader("Authorization") String token, @RequestBody TakeLoanRequest request){
        try {
            TakeLoanResponse response = loanService.requestLoan(request,token);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            ErrorResponse response = ErrorResponse.builder()
                    .message("Failed to get loan: "+e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
            return ResponseEntity.badRequest().body(response);
        }

    }


    @GetMapping("/getLoans")
    public ResponseEntity<List<LoanStatusResponse>> getMyLoans(@RequestHeader("Authorization") String token, @RequestParam(required = false) LoanStatus status){
        List<LoanStatusResponse> response = loanService.getMyLoans(token, status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payLoan")
    public ResponseEntity<?> payTermLoan(@RequestHeader("Authorization") String token, @RequestBody PaymentRequest paymentRequest){
        try {
            PaymentResponse response = loanService.payTermLoan(token, paymentRequest);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            ErrorResponse response = ErrorResponse.builder()
                    .message("Failed to pay loan: "+e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
            return  ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/approveLoan")
    public ResponseEntity<?> approveLoan(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> requestBody){
        try {
            String loanId = requestBody.get("loanId");
            loanService.approveLoan(token, loanId);
            MessageResponse response = MessageResponse.builder()
                    .message("Yay loan approved successfully. Loan ID: "+loanId)
                    .status(HttpStatus.OK)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/getLoanById")
    public ResponseEntity<?> getLoanById(@RequestHeader("Authorization") String token, @RequestParam String loanId){
        try {
            LoanStatusResponse response = loanService.getLoanById(token, loanId);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

}
