package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.MembershipApplications;
import com.starnoh.sacco_management.enums.ApplicationStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipApplicationsRepository extends JpaRepository<MembershipApplications , Long> {

    Optional<MembershipApplications>
    findByUserIdAndApplicationStatus(
            Long userId,
            ApplicationStatus applicationStatus
    );

    Optional<MembershipApplications>
    findByUserId(
            Long userId
    );

    // The EntityGraph forces a SQL JOIN, fetching the user data immediately in 1 query
    @EntityGraph(attributePaths = {"user", "user.role"})

    Page<MembershipApplications> findByApplicationStatus(ApplicationStatus status, Pageable pageable);

    boolean existsByUserIdAndApplicationStatus(
            Long userId,
            ApplicationStatus applicationStatus
    );
}
