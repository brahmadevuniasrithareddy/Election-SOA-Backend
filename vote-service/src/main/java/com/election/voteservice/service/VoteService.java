package com.election.voteservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.election.voteservice.client.AuditClient;
import com.election.voteservice.client.ElectionClient;
import com.election.voteservice.entity.Vote;
import com.election.voteservice.entity.VoterParticipation;
import com.election.voteservice.repository.VoteRepository;
import com.election.voteservice.repository.VoterParticipationRepository;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoterParticipationRepository participationRepository;
    private final AuditClient auditClient;
    private final ElectionClient electionClient;

    public VoteService(
            VoteRepository voteRepository,
            VoterParticipationRepository participationRepository,
            AuditClient auditClient,
            ElectionClient electionClient) {

        this.voteRepository = voteRepository;
        this.participationRepository = participationRepository;
        this.auditClient = auditClient;
        this.electionClient = electionClient;
    }

    @Transactional
    public Vote castVote(
            Long electionId,
            Long candidateId,
            Long voterId) {

        // Check election
        Map<String, Object> election =
                electionClient.getElection(electionId);

        String status =
                String.valueOf(election.get("status"));

        if (!"ACTIVE".equals(status)) {
            throw new RuntimeException(
                    "Voting is not allowed. Election is not ACTIVE.");
        }

        // Check candidate
        Map<String, Object> candidate =
                electionClient.getCandidate(candidateId);

        Object candidateElectionId =
                candidate.get("electionId");

        if (candidateElectionId == null
                || !String.valueOf(candidateElectionId)
                        .equals(String.valueOf(electionId))) {

            throw new RuntimeException(
                    "Candidate does not belong to this election.");
        }

        // Check whether voter has already voted
        if (participationRepository
                .existsByElectionIdAndVoterId(
                        electionId,
                        voterId)) {

            throw new RuntimeException(
                    "Voter has already voted in this election");
        }

        // Create vote
        Vote vote = new Vote();

        vote.setElectionId(electionId);
        vote.setCandidateId(candidateId);
        vote.setVoteReference(
                "VOTE-" + UUID.randomUUID());
        vote.setCreatedAt(LocalDateTime.now());

        Vote savedVote =
                voteRepository.save(vote);

        // Record voter participation
        VoterParticipation participation =
                new VoterParticipation();

        participation.setElectionId(electionId);
        participation.setVoterId(voterId);
        participation.setVotedAt(
                LocalDateTime.now());

        participationRepository.save(participation);

        // Create audit record
        String auditData =
                "electionId=" + electionId
                + ",voteReference="
                + vote.getVoteReference();

        auditClient.recordVote(auditData);

        return savedVote;
    }

    public boolean hasVoted(
            Long electionId,
            Long voterId) {

        return participationRepository
                .existsByElectionIdAndVoterId(
                        electionId,
                        voterId);
    }

    public List<Vote> getAllVotes() {

        return voteRepository.findAll();
    }

    public List<Object[]> getResults(
            Long electionId) {

        Map<String, Object> election =
                electionClient.getElection(electionId);

        String status =
                String.valueOf(election.get("status"));

        if (!"ENDED".equals(status)) {
            throw new RuntimeException(
                    "Election has not ended. Results are not available.");
        }

        return voteRepository.getResults(
                electionId);
    }
}