package com.aspire.aspireproject.repository;

import com.aspire.aspireproject.model.loan.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoanRepository extends MongoRepository<Loan,String> {
}
