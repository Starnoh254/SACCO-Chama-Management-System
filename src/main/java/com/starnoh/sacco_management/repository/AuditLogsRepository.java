package com.starnoh.sacco_management.repository;

import com.starnoh.sacco_management.entity.AuditLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogsRepository extends JpaRepository<AuditLogs , Long> {
}
