package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.Contributions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributionsRepository extends JpaRepository<Contributions , Long> {
}
