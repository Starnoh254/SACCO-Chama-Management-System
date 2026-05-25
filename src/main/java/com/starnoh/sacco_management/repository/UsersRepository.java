package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users , Long> {
}
