package com.payconiq.model;

import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;

public interface DataPersistence {

    // data is loaded on start and saved on shutdown
    // and saved

    @EventListener(ContextStartedEvent.class)
    void loadOnStart();

    @PreDestroy
    void saveOnShutdown();
}
