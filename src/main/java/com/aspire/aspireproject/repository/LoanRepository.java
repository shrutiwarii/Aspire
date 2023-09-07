package com.aspire.aspireproject.repository;

import com.aspire.aspireproject.model.loan.Loan;
import com.aspire.aspireproject.model.loan.LoanStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends MongoRepository<Loan,String> {
    Optional<List<Loan>> findByUsername(String username);

    Optional<List<Loan>> findByUsernameAndStatus(String username, LoanStatus status);

    @NotNull
    Optional<Loan> findById(@NotNull String Id);

    Optional<Loan> findByIdempotencyTokenAndStatus(String token, LoanStatus status);
}
