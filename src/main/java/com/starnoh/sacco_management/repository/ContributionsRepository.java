package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.Contributions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ContributionsRepository extends JpaRepository<Contributions , Long> {

    // Check for duplicate : same member , same date , same amount
    // used to prevent accidental double - entry
    boolean existsByMemberIdAndContributionDateAndAmount(
            Long memberId, LocalDate date , BigDecimal amount
    );

    // used by future GET /contributions endpoints
    Page<Contributions> findByMemberId(Long memberId, Pageable pageable);
}
