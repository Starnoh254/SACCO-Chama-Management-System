package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.LoanRepayments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepaymentsRepository extends JpaRepository<LoanRepayments , Long> {
}
