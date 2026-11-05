package com.election.auditservice.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.election.auditservice.entity.AuditLog;
import com.election.auditservice.repository.AuditLogRepository;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog createAuditLog(
            String eventType,
            String eventData) {

        List<AuditLog> logs =
                auditLogRepository.findAllByOrderByIdAsc();

        String previousHash = "GENESIS";

        if (!logs.isEmpty()) {
            AuditLog previousLog =
                    logs.get(logs.size() - 1);

            previousHash =
                    previousLog.getCurrentHash();
        }

        LocalDateTime createdAt =
                LocalDateTime.now();

        String dataToHash =
                eventType
                + eventData
                + previousHash
                + createdAt.toString();

        String currentHash =
                generateSHA256(dataToHash);

        AuditLog auditLog =
                new AuditLog();

        auditLog.setEventType(eventType);
        auditLog.setEventData(eventData);
        auditLog.setPreviousHash(previousHash);
        auditLog.setCurrentHash(currentHash);
        auditLog.setCreatedAt(createdAt);

        return auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getAllAuditLogs() {

        return auditLogRepository
                .findAllByOrderByIdAsc();
    }

    // Verify the complete audit hash chain
    public boolean verifyAuditChain() {

        List<AuditLog> logs =
                auditLogRepository
                        .findAllByOrderByIdAsc();

        String expectedPreviousHash = "GENESIS";

        for (AuditLog log : logs) {

            // Check previous hash
            if (!expectedPreviousHash.equals(
                    log.getPreviousHash())) {

                return false;
            }

            // Recreate the hash
            String dataToHash =
                    log.getEventType()
                    + log.getEventData()
                    + log.getPreviousHash()
                    + log.getCreatedAt().toString();

            String recalculatedHash =
                    generateSHA256(dataToHash);

            // Compare with stored hash
            if (!recalculatedHash.equals(
                    log.getCurrentHash())) {

                return false;
            }

            expectedPreviousHash =
                    log.getCurrentHash();
        }

        return true;
    }

    private String generateSHA256(String input) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            input.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder hexString =
                    new StringBuilder();

            for (byte b : hash) {

                String hex =
                        Integer.toHexString(
                                0xff & b
                        );

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to generate SHA-256 hash",
                    e
            );
        }
    }
}