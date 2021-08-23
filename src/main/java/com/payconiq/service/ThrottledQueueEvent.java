package com.payconiq.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
@Setter
public class ThrottledQueueEvent extends ApplicationEvent {

    private List<String> backendResult;
    private String endpoint;

    public ThrottledQueueEvent(Object source, String endpoint, List<String> backendResult) {

        super(source);
        this.endpoint = endpoint;
        this.backendResult = backendResult;
    }
}
