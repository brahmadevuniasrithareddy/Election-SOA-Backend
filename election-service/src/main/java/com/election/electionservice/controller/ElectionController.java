package com.election.electionservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.election.electionservice.entity.Election;
import com.election.electionservice.service.ElectionService;

@RestController
@RequestMapping("/api/elections")
public class ElectionController {

    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping
    public ResponseEntity<List<Election>> getAllElections() {
        return ResponseEntity.ok(
                electionService.getAllElections()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Election> getElectionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                electionService.getElectionById(id)
        );
    }

    @PostMapping
    public ResponseEntity<Election> createElection(
            @RequestBody Election election) {

        return ResponseEntity.ok(
                electionService.createElection(election)
        );
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Election> startElection(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                electionService.startElection(id)
        );
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<Election> endElection(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                electionService.endElection(id)
        );
    }
}