package com.wishly.pasteservice.generator;

import com.wishly.pasteservice.exception.*;
import com.wishly.common.generator.HashGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class RemoteHashGenerator implements HashGenerator {
    private final RestTemplate restTemplate;

    @Value("${hash.generator.service.url:http://hash-generator-service:8081}")
    private String hashServiceUrl;

    private static final String HASH_ENDPOINT = "/api/hash";

    public RemoteHashGenerator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String generate(int length) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(hashServiceUrl)
                    .path(HASH_ENDPOINT)
                    .queryParam("length", length)
                    .build()
                    .toUriString();

            log.debug("Requesting hash from: {}", url);

            String hash = restTemplate.getForObject(url, String.class);

            if (hash == null || hash.isEmpty()) {
                log.error("Empty hash received from hash-generator service");
                throw new HashGenerationException("Received empty hash from remote service");
            }

            log.debug("Received hash: {}", hash);
            return hash;
        } catch (RestClientException e) {
            log.error("Failed to generate hash from remote service: {}", e.getMessage());
            throw new HashGenerationException("Remote hash generator unavailable", e);
        }
    }
}
