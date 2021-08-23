package com.payconiq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class AppConfig {

    @Value("${queue.capacity}")
    private int queueCapacity;

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080")
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofMillis(1000))))
                .build();
    }

    @Bean
    public BlockingQueue<String> shipmentsQueue() {

        return new ArrayBlockingQueue<>(queueCapacity);
    }

    @Bean
    public BlockingQueue<String> trackQueue() {

        return new ArrayBlockingQueue<String>(queueCapacity);
    }

    @Bean
    public BlockingQueue<String> pricingQueue() {

        return new ArrayBlockingQueue<String>(queueCapacity);
    }

    @Bean
    public ConcurrentHashMap<String, List<String>> shipmentsCache() {

        return new ConcurrentHashMap<String, List<String>>();
    }

    @Bean
    public ConcurrentHashMap<String, String> trackCache() {

        return new ConcurrentHashMap<String, String>();
    }

    @Bean
    public ConcurrentHashMap<String, Double> pricingCache() {

        return new ConcurrentHashMap<String, Double>();
    }
}
