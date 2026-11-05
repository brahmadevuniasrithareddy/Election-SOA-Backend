package com.election.electionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.election.electionservice.entity.Election;

public interface ElectionRepository extends JpaRepository<Election, Long> {
}