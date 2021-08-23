package com.payconiq.controller;

import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AggregatedQuery {

    private List<String> shipments;
    private List<String> track;
    private List<String> pricing;

    private List<String> originalShipments;
    private List<String> originalTrack;
    private List<String> originalPricing;

    private static final String NUMERIC_ID = "\\d{9}";
    private static final String COUNTRY_ID = "[A-Z]{2}";

    private List<String> filterIdsByRegex(List<String> list, String regex) {

        return list.stream()
                    .filter(el -> el.matches(regex)).collect(Collectors.toList());
    }

    private List<String> removeDuplicateIds(List<String> list) {

        Set<String> set = new HashSet<String>(list);

        return set.stream().collect(Collectors.toList());
    }

    private List<String> convertNullToEmptyList(List<String> list) {

        if(list == null) list = Collections.<String>emptyList();

        return list;
    }

    private List<String> executeCleanupSteps(List<String> list, String regex) {

        list = convertNullToEmptyList(list);
        list = removeDuplicateIds(list);
        list = filterIdsByRegex(list, regex);

        return list;
    }

    public AggregatedQuery toPreparedQuery() {

        originalShipments = convertNullToEmptyList(shipments);
        originalTrack = convertNullToEmptyList(track);
        originalPricing = convertNullToEmptyList(pricing);

        pricing = executeCleanupSteps(pricing, COUNTRY_ID);
        track = executeCleanupSteps(track, NUMERIC_ID);
        shipments = executeCleanupSteps(shipments, NUMERIC_ID);

        return this;
    }
}
