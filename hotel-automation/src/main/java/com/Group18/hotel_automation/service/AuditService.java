package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.entity.AuditLog;
import com.Group18.hotel_automation.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String adminEmail,
                    String action,
                    String entityType,
                    Long entityId) {

        AuditLog log = new AuditLog();
        log.setAdminEmail(adminEmail);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);

        auditLogRepository.save(log);
    }
}