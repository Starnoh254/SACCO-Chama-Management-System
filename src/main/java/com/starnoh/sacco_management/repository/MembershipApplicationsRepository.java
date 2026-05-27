package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.MembershipApplications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipApplicationsRepository extends JpaRepository<MembershipApplications , Long> {
}
