package com.gym.service;

import com.gym.model.AuditLog;
import com.gym.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logAction(String actionType, String targetEntity, String details) {
        String adminEmail = "SYSTEM";
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            adminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        }
        
        AuditLog log = new AuditLog(actionType, targetEntity, adminEmail, LocalDateTime.now(), details);
        auditLogRepository.save(log);
    }
    
    public java.util.List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}
