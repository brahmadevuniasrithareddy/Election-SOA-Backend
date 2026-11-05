package com.election.electionservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.election.electionservice.entity.Party;
import com.election.electionservice.repository.PartyRepository;

@Service
public class PartyService {

    private final PartyRepository partyRepository;

    public PartyService(PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    public List<Party> getAllParties() {
        return partyRepository.findAll();
    }

    public Party getPartyById(Long id) {
        return partyRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Party not found"));
    }

    public Party createParty(Party party) {
        return partyRepository.save(party);
    }

    public Party updateParty(Long id, Party updatedParty) {

        Party party = getPartyById(id);

        party.setName(updatedParty.getName());
        party.setAbbreviation(updatedParty.getAbbreviation());
        party.setDescription(updatedParty.getDescription());

        return partyRepository.save(party);
    }

    public void deleteParty(Long id) {

        Party party = getPartyById(id);

        partyRepository.delete(party);
    }
}