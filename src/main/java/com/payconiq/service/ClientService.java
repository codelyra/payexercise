package com.payconiq.service;

import com.payconiq.controller.AggregatedQuery;
import com.payconiq.controller.AggregatedResult;
import org.springframework.stereotype.Service;

@Service
public interface ClientService {

    AggregatedResult processQuery(AggregatedQuery query);
}
