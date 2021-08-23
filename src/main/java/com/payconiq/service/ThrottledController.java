package com.payconiq.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payconiq.controller.AggregatedResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@RestController
public class ThrottledController implements ApplicationListener<ThrottledQueueEvent> {

    @Autowired
    private WebClient webClient;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private Mono<String> callBackend(String endpoint, List<String> params) {

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

    private Map<String, List<String>> deserializeShipments(String asyncResult) {

        Map<String, List<String>> result;
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.readValue(asyncResult, new TypeReference<Map<String, List<String>>>() {});
        } catch (Exception exception) {
            result = null;
        }
        return result;
    }

    private Map<String, String> deserializeTrack(String asyncResult) {

        Map<String, String> result;
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.readValue(asyncResult, new TypeReference<Map<String, String>>() {});
        } catch (Exception exception) {
            result = null;
        }
        return result;
    }

    private Map<String, Double> deserializePricing(String asyncResult) {

        Map<String, Double> result;
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.readValue(asyncResult, new TypeReference<Map<String, Double>>(){});
        } catch (Exception exception) {
            result = null;
        }
        return result;
    }

    @Override
    public void onApplicationEvent(ThrottledQueueEvent throttledEvent) {

        String endpoint = throttledEvent.getEndpoint();

        Mono<String> asyncCall = callBackend(endpoint, throttledEvent.getBackendResult());
        String asyncResult;
        try {
             asyncResult = asyncCall.block();
        } catch (Exception exc) {
            asyncResult = null;
        }

        AggregatedResult result = new AggregatedResult();

        if(endpoint.equals("shipments")) {
            result.setShipments(deserializeShipments(asyncResult));
            applicationEventPublisher.publishEvent(new ThrottledCacheEvent(this, endpoint, result));
        }

        if(endpoint.equals("track")) {
            result.setTrack(deserializeTrack(asyncResult));
            applicationEventPublisher.publishEvent(new ThrottledCacheEvent(this, endpoint, result));
        }

        if(endpoint.equals("pricing")) {
            result.setPricing(deserializePricing(asyncResult));
            applicationEventPublisher.publishEvent(new ThrottledCacheEvent(this, endpoint, result));
        }

    }
}
