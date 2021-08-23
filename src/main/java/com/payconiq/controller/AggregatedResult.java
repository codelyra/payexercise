package com.payconiq.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class AggregatedResult {

    Map<String, List<String>> shipments;
    Map<String, String> track;
    Map<String, Double> pricing;

    private List<String> convertNullToEmptyList(List<String> list) {

        if(list == null) list = Collections.<String>emptyList();

        return list;
    }

    public AggregatedResult toPreparedResult(AggregatedQuery query) {

        if(shipments != null) {
            query.getOriginalShipments().stream().forEach(el -> {
                if(!shipments.containsKey(el)) shipments.put(el, null);
            });
        }
        if(track != null) {
            query.getOriginalTrack().stream().forEach(el -> {
                if(!track.containsKey(el)) track.put(el, null);
            });
        }
        if(pricing != null) {
            query.getOriginalPricing().stream().forEach(el -> {
                if(!pricing.containsKey(el)) pricing.put(el, null);
            });
        }

        return this;
    }
}
