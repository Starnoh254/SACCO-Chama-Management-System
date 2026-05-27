package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles , Long> {

    boolean existsByName(String name);

    Optional<Roles> findByName(String name);
}
