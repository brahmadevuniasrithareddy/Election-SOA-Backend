package com.election.voteservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.election.voteservice.entity.VoterParticipation;

public interface VoterParticipationRepository
        extends JpaRepository<VoterParticipation, Long> {

    boolean existsByElectionIdAndVoterId(
            Long electionId,
            Long voterId
    );

    Optional<VoterParticipation> findByElectionIdAndVoterId(
            Long electionId,
            Long voterId
    );
}