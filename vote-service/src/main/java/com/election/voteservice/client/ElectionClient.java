package com.election.voteservice.client;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ElectionClient {

    private final RestClient restClient;

    public ElectionClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public Map<String, Object> getElection(Long electionId) {

        return restClient.get()
                .uri("http://localhost:8082/api/elections/" + electionId)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> getCandidate(Long candidateId) {

        return restClient.get()
                .uri("http://localhost:8082/api/candidates/" + candidateId)
                .retrieve()
                .body(Map.class);
    }
}