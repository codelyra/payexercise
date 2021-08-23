package com.payconiq.service;

import com.payconiq.controller.AggregatedResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Component
public class ThrottledCaches implements ApplicationListener<ThrottledCacheEvent> {

    @Autowired
    private ConcurrentHashMap<String, List<String>> shipmentsCache;

    @Autowired
    private ConcurrentHashMap<String, String> tracksCache;

    @Autowired
    private ConcurrentHashMap<String, Double> pricingCache;

    public Map<String, List<String>> retrieveShipments(List<String> shipments) {

        Map<String, List<String>> result = new HashMap<String, List<String>>();
        shipments.stream().forEach(el -> result.put(el, null));
        while(result.containsValue(null)) {
            shipments.stream().forEach(el -> result.put(el, shipmentsCache.get(el)));
        }
        return result;
    }

    public Map<String, String> retrieveTrack(List<String> track) {

        Map<String, String> result = new HashMap<String, String>();
        track.stream().forEach(el -> result.put(el, null));
        while(result.containsValue(null)) {
            track.stream().forEach(el -> result.put(el, tracksCache.get(el)));
        }
        return result;
    }

    public Map<String, Double> retrievePricing(List<String> pricing) {

        Map<String, Double> result = new HashMap<String, Double>();
        pricing.stream().forEach(el -> result.put(el, null));
        while(result.containsValue(null)) {
            pricing.stream().forEach(el -> result.put(el, pricingCache.get(el)));
        }
        return result;
    }

    @Override
    public void onApplicationEvent(ThrottledCacheEvent cachedEvent) {

        String endpoint = cachedEvent.getEndpoint();
        AggregatedResult result  = cachedEvent.getBackendResult();

        if(endpoint.equals("shipments")) {
            shipmentsCache.putAll(result.getShipments());
        }

        if(cachedEvent.getEndpoint().equals("track")) {
            for(Map.Entry<String, String> entry: result.getTrack().entrySet()) {
                tracksCache.put(entry.getKey(), entry.getValue());
            }
        }

        if(cachedEvent.getEndpoint().equals("pricing")) {
            for(Map.Entry<String, Double> entry: result.getPricing().entrySet()) {
                pricingCache.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
