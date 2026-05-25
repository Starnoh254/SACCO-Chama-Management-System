package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.FinancialReports;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialReportsRepository extends JpaRepository<FinancialReports , Long> {
}
