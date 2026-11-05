package com.election.electionservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.election.electionservice.entity.Candidate;
import com.election.electionservice.repository.CandidateRepository;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public List<Candidate> getCandidatesByElection(Long electionId) {
        return candidateRepository.findByElectionId(electionId);
    }

    public Candidate getCandidateById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Candidate not found"));
    }

    public Candidate createCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    public Candidate updateCandidate(Long id, Candidate updatedCandidate) {

        Candidate candidate = getCandidateById(id);

        candidate.setName(updatedCandidate.getName());
        candidate.setDescription(updatedCandidate.getDescription());
        candidate.setElectionId(updatedCandidate.getElectionId());
        candidate.setPartyId(updatedCandidate.getPartyId());

        return candidateRepository.save(candidate);
    }

    public void deleteCandidate(Long id) {

        Candidate candidate = getCandidateById(id);

        candidateRepository.delete(candidate);
    }
}