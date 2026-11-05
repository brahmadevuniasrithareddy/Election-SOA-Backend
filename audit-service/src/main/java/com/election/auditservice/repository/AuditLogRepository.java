package com.election.auditservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.election.auditservice.entity.AuditLog;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByOrderByIdAsc();
}