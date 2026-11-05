package com.election.voteservice.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AuditClient {

    private final RestClient restClient;

    public AuditClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void recordVote(String eventData) {

        restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8084)
                        .path("/api/audit")
                        .queryParam("eventType", "VOTE_CAST")
                        .queryParam("eventData", eventData)
                        .build())
                .retrieve()
                .toBodilessEntity();
    }
}