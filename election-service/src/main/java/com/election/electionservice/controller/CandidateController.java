package com.election.electionservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.election.electionservice.entity.Candidate;
import com.election.electionservice.service.CandidateService;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @GetMapping
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        return ResponseEntity.ok(
                candidateService.getAllCandidates()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidateById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                candidateService.getCandidateById(id)
        );
    }

    @GetMapping("/election/{electionId}")
    public ResponseEntity<List<Candidate>> getCandidatesByElection(
            @PathVariable Long electionId) {

        return ResponseEntity.ok(
                candidateService.getCandidatesByElection(electionId)
        );
    }

    @PostMapping
    public ResponseEntity<Candidate> createCandidate(
            @RequestBody Candidate candidate) {

        return ResponseEntity.ok(
                candidateService.createCandidate(candidate)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidate> updateCandidate(
            @PathVariable Long id,
            @RequestBody Candidate candidate) {

        return ResponseEntity.ok(
                candidateService.updateCandidate(id, candidate)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(
            @PathVariable Long id) {

        candidateService.deleteCandidate(id);

        return ResponseEntity.noContent().build();
    }
}