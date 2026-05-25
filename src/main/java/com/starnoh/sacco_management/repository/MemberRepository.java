package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Members, Long> {
}
