package com.payconiq.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@Component
@Scope("prototype")
public class ThrottledTime {

    private Instant time = null;

    public synchronized void updateTime() {

        time = Instant.now();
    }

    public boolean isTime() {

        if(time == null) {
            return false;
        }

        return Duration.between(getTime(), Instant.now()).getSeconds() > 5;
    }
}
