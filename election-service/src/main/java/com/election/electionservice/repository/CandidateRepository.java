package com.election.electionservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.election.electionservice.entity.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findByElectionId(Long electionId);

}