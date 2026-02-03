package com.tictactoe.ui;

import com.tictactoe.contracts.SessionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class SessionClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String sessionBaseUrl;

    public SessionClient(@Value("${session.base-url:http://localhost:8082}") String sessionBaseUrl) {
        this.sessionBaseUrl = sessionBaseUrl.replaceAll("/$", "");
    }

    public SessionDto createSession(UUID correlationId) {
        String url = sessionBaseUrl + "/sessions";
        HttpHeaders headers = new HttpHeaders();
        if (correlationId != null) headers.set("X-Correlation-Id", correlationId.toString());
        ResponseEntity<SessionDto> res = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(headers), SessionDto.class);
        return res.getBody();
    }

    public void startSimulation(UUID sessionId, UUID correlationId) {
        String url = sessionBaseUrl + "/sessions/" + sessionId + "/simulate";
        HttpHeaders headers = new HttpHeaders();
        if (correlationId != null) headers.set("X-Correlation-Id", correlationId.toString());
        restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(headers), SessionDto.class);
    }

    public SessionDto getSession(UUID sessionId) {
        String url = sessionBaseUrl + "/sessions/" + sessionId;
        return restTemplate.getForObject(url, SessionDto.class);
    }
}
