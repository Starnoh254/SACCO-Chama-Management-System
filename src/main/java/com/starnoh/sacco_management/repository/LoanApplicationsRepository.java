package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.LoanApplications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanApplicationsRepository extends JpaRepository<LoanApplications , Long> {
}
