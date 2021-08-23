package com.payconiq.service;

import com.payconiq.controller.AggregatedQuery;
import com.payconiq.controller.AggregatedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThrottledClient implements ClientService {

    @Autowired
    private ThrottledQueues throttledQueues;

    @Autowired
    private ThrottledCaches cachedResults;

    @Override
    public AggregatedResult processQuery(AggregatedQuery query) {

        throttledQueues.enqueueShipments(query.getShipments());
        throttledQueues.enqueueTrack(query.getTrack());
        throttledQueues.enqueuePricing(query.getPricing());

        AggregatedResult result = new AggregatedResult();
        if(query.getShipments().size() > 0) {
            result.setShipments(cachedResults.retrieveShipments(query.getShipments()));
        } else {
            result.setShipments(null);
        }
        if(query.getTrack().size() > 0) {
            result.setTrack(cachedResults.retrieveTrack(query.getTrack()));
        } else {
            result.setTrack(null);
        }
        if(query.getPricing().size() > 0) {
            result.setPricing(cachedResults.retrievePricing(query.getPricing()));
        } else {
            result.setPricing(null);
        }

        return result;
    }
}
