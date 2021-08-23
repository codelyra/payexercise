package com.payconiq.service;

import com.payconiq.controller.AggregatedResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ThrottledCacheEvent extends ApplicationEvent {

    private AggregatedResult backendResult;

    private String endpoint;

    public ThrottledCacheEvent(Object source, String endpoint, AggregatedResult backendResult) {

        super(source);
        this.endpoint = endpoint;
        this.backendResult = backendResult;
    }
}
