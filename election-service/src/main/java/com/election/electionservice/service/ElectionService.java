package com.election.electionservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.election.electionservice.entity.Election;
import com.election.electionservice.entity.ElectionStatus;
import com.election.electionservice.repository.ElectionRepository;

@Service
public class ElectionService {

    private final ElectionRepository electionRepository;

    public ElectionService(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
    }

    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    public Election getElectionById(Long id) {
        return electionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Election not found"));
    }

    public Election createElection(Election election) {

        election.setStatus(ElectionStatus.DRAFT);

        return electionRepository.save(election);
    }

    public Election startElection(Long id) {

        Election election = getElectionById(id);

        election.setStatus(ElectionStatus.ACTIVE);

        return electionRepository.save(election);
    }

    public Election endElection(Long id) {

        Election election = getElectionById(id);

        election.setStatus(ElectionStatus.ENDED);

        return electionRepository.save(election);
    }
}