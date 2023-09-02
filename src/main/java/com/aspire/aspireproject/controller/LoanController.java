package com.aspire.aspireproject.controller;

import com.aspire.aspireproject.dao.request.TakeLoanRequest;
import com.aspire.aspireproject.dao.response.TakeLoanResponse;
import com.aspire.aspireproject.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/aspire/v1/project")
public class LoanController {
    @Autowired
    private LoanService loanService;
    @PostMapping("/requestLoan")
    public ResponseEntity<TakeLoanResponse> requestLoan(@RequestHeader("Authorization") String token, @RequestBody TakeLoanRequest request){
        TakeLoanResponse response = loanService.requestLoan(request,token.split(" ")[1]);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/getMyLoans")
    public ResponseEntity<String> getMyLoans(String username){
        return ResponseEntity.ok("Successful and your loan id is: Currently your loan is in PENDING state. You will get a notification when it moves to APPROVED");

    }
    @PostMapping("/approveMyLoans")
    public ResponseEntity<String> approveLoan(String username){
        return ResponseEntity.ok("Successful and your loan id is: Currently your loan is in PENDING state. You will get a notification when it moves to APPROVED");

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
