package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationsRepository extends JpaRepository<Notifications , Long> {
}
