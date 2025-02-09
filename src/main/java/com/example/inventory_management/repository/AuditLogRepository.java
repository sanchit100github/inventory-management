package com.example.inventory_management.repository;

import com.example.inventory_management.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.*;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    
    // Find logs by the user who has access to them (role-based filtering)
    List<AuditLog> findByAccessToContaining(String userEmail);
    
    // You can add more custom queries if needed, such as finding logs by action type
    List<AuditLog> findByAction(String action);
    
    List<AuditLog> findByActionBy(String actionBy);

    // You could also filter by timestamp or other fields if needed
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
}
