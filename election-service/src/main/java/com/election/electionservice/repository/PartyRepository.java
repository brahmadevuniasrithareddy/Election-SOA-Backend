package com.election.electionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.election.electionservice.entity.Party;

public interface PartyRepository extends JpaRepository<Party, Long> {
}