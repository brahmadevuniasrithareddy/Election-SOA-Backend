package com.election.auditservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.election.auditservice.entity.AuditLog;
import com.election.auditservice.service.AuditService;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    // Create an audit record
    @PostMapping
    public ResponseEntity<AuditLog> createAuditLog(
            @RequestParam String eventType,
            @RequestParam String eventData) {

        AuditLog auditLog =
                auditService.createAuditLog(
                        eventType,
                        eventData
                );

        return ResponseEntity.ok(auditLog);
    }

    // Get all audit records
    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {

        return ResponseEntity.ok(
                auditService.getAllAuditLogs()
        );
    }

    // Verify the complete SHA-256 audit chain
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>>
            verifyAuditChain() {

        boolean valid =
                auditService.verifyAuditChain();

        Map<String, Object> response =
                new HashMap<>();

        response.put("valid", valid);

        if (valid) {
            response.put(
                    "message",
                    "Audit chain is valid. No tampering detected."
            );
        } else {
            response.put(
                    "message",
                    "Audit chain verification failed. Possible tampering detected."
            );
        }

        return ResponseEntity.ok(response);
    }
}