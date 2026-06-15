package com.prettyflights.repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AuditRepository {
    private Queue<AuditLog> auditLogs;
    
    public AuditRepository() {
        this.auditLogs = new ConcurrentLinkedQueue<>();
    }
    
    public void logAction(String action, String details) {
        AuditLog log = new AuditLog(action, details, LocalDateTime.now());
        auditLogs.add(log);
        System.out.println("[AUDIT] " + log);
    }
    
    public List<AuditLog> getAllLogs() {
        return new ArrayList<>(auditLogs);
    }
    
    public static class AuditLog {
        private String action;
        private String details;
        private LocalDateTime timestamp;
        
        public AuditLog(String action, String details, LocalDateTime timestamp) {
            this.action = action;
            this.details = details;
            this.timestamp = timestamp;
        }
        
        @Override
        public String toString() {
            return String.format("%s - %s: %s", timestamp, action, details);
        }
    }
}
