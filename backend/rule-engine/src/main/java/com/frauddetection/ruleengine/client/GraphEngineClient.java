package com.frauddetection.ruleengine.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GraphEngineClient {

    private final RestTemplate restTemplate;
    private final String graphEngineUrl;

    public GraphEngineClient(RestTemplate restTemplate,
                              @Value("${graph-engine.url}") String graphEngineUrl) {
        this.restTemplate = restTemplate;
        this.graphEngineUrl = graphEngineUrl;
    }

    public int countCycles(String accountId) {
        try {
            List<?> cycles = restTemplate.getForObject(
                    graphEngineUrl + "/api/graph/accounts/{accountId}/cycles",
                    List.class,
                    Map.of("accountId", accountId));
            return cycles != null ? cycles.size() : 0;
        } catch (RestClientException e) {
            log.warn("Impossible d'interroger graph-engine pour le compte [{}] : {}", accountId, e.getMessage());
            return 0;
        }
    }
}
