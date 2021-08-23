package com.payconiq.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payconiq.controller.AggregatedQuery;
import com.payconiq.controller.AggregatedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.List;
import java.util.Map;

@Service
public class NonBlockingClient implements ClientService {

    @Autowired
    private NonBlockingController nonBlockingController;

    @Override
    public AggregatedResult processQuery(AggregatedQuery query) {

        Mono<String> shipmentsResult = nonBlockingController.callBackend("shipments", query.getShipments());
        Mono<String> trackResult = nonBlockingController.callBackend("track", query.getTrack());
        Mono<String> pricingResult = nonBlockingController.callBackend("pricing", query.getPricing());
        Mono<Tuple3<String, String, String>> asyncResult = Mono.zip(shipmentsResult, trackResult, pricingResult);

        AggregatedResult result = new AggregatedResult();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Tuple3<String, String, String> val = asyncResult.block();
            result.setShipments(mapper.readValue(val.getT1(),
                    new TypeReference<Map<String, List<String>>>() {}));
            result.setTrack(mapper.readValue(val.getT2(),
                    new TypeReference<Map<String, String>>() {}));
            result.setPricing(mapper.readValue(val.getT3(),
                    new TypeReference<Map<String, Double>>() {}));
        } catch(Exception exc) {
            result.setShipments(null);
            result.setTrack(null);
            result.setPricing(null);
        }

        return result;
    }
}
