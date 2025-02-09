package com.example.inventory_management.service;

import com.example.inventory_management.model.AuditLog;
import com.example.inventory_management.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    // Save a new audit log
    public AuditLog saveAudit(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    // Get all logs for a user (based on who performed the action)
    public List<AuditLog> getLogsByUser(String actionBy) {
        return auditLogRepository.findByActionBy(actionBy);
    }

    // Get logs based on the action type (e.g., "DELETE_MANAGER")
    public List<AuditLog> getLogsByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    // Get logs within a certain time range
    public List<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }

    // Get logs that a user has access to (filtered by user role or specific access list)
    public List<AuditLog> getLogsByAccess(String userEmail) {
        return auditLogRepository.findByAccessToContaining(userEmail);
    }

    // Get all audit logs (useful for Admins to access all logs)
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}
