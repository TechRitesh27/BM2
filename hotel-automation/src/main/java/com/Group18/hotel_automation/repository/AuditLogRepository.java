package com.Group18.hotel_automation.repository;

import com.Group18.hotel_automation.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}