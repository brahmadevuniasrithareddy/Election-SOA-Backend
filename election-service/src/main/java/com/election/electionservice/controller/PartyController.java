package com.election.electionservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.election.electionservice.entity.Party;
import com.election.electionservice.service.PartyService;

@RestController
@RequestMapping("/api/parties")
public class PartyController {

    private final PartyService partyService;

    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    @GetMapping
    public ResponseEntity<List<Party>> getAllParties() {
        return ResponseEntity.ok(
                partyService.getAllParties()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Party> getPartyById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                partyService.getPartyById(id)
        );
    }

    @PostMapping
    public ResponseEntity<Party> createParty(
            @RequestBody Party party) {

        return ResponseEntity.ok(
                partyService.createParty(party)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Party> updateParty(
            @PathVariable Long id,
            @RequestBody Party party) {

        return ResponseEntity.ok(
                partyService.updateParty(id, party)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParty(
            @PathVariable Long id) {

        partyService.deleteParty(id);

        return ResponseEntity.noContent().build();
    }
}