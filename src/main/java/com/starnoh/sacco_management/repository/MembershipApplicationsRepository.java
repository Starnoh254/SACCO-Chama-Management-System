package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.MembershipApplications;
import com.starnoh.sacco_management.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipApplicationsRepository extends JpaRepository<MembershipApplications , Long> {

    Optional<MembershipApplications>
    findByUserIdAndApplicationStatus(
            Long userId,
            ApplicationStatus applicationStatus
    );

    boolean existsByUserIdAndApplicationStatus(
            Long userId,
            ApplicationStatus applicationStatus
    );
}
