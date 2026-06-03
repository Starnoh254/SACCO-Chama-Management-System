package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MemberRepository extends JpaRepository<Members, Long> , JpaSpecificationExecutor<Members> {
}
