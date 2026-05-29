package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users , Long> {


    // Derived query: Spring automatically writes "SELECT * FROM users WHERE email = ?"
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);

}
