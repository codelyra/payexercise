package com.payconiq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class NonBlockingController {

    @Autowired
    private WebClient webClient;

    public Mono<String> callBackend(String endpoint, List<String> params) {

        String csv = String.join(",", params);
        try {
            return webClient
                    .get()
                    .uri("/{endpoint}?q={params}", endpoint, csv)
                    .retrieve()
                    .bodyToMono(String.class);
        } catch(Exception exc) {
            return Mono.empty();
        }
    }
}
