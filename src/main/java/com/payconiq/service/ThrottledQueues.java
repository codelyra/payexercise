package com.payconiq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Component
public class ThrottledQueues {

    @Value("${queue.capacity}")
    private int queueCapacity;

    @Autowired
    private java.util.concurrent.BlockingQueue<String> shipmentsQueue;

    @Autowired
    private java.util.concurrent.BlockingQueue<String> trackQueue;

    @Autowired
    private java.util.concurrent.BlockingQueue<String> pricingQueue;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Value("${queue.time}")
    private int queueTime;

    private ThrottledTime shipmentsTime;
    private ThrottledTime trackTime;
    private ThrottledTime pricingTime;

    @Autowired()
    private void setShipmentsTime(ThrottledTime shipmentsTime) {

        this.shipmentsTime = shipmentsTime;
    }

    @Autowired()
    private void setTrackTime(ThrottledTime trackTime) {

        this.trackTime = trackTime;
    }
        @Autowired()
    private void setPricingTime(ThrottledTime pricingTime) {

        this.pricingTime = pricingTime;
    }

    @Async
    private void enqueueList(BlockingQueue<String> queue, List<String> list, ThrottledTime time) {

        for (String el : list) {
            try {
                queue.put(el);
                time.updateTime();
            } catch (InterruptedException e) {
                e.getMessage();
            }
        }
    }

    public void enqueueShipments(List<String> shipmentsList) {


        enqueueList(shipmentsQueue, shipmentsList, shipmentsTime);
    }

    public void enqueueTrack(List<String> trackList) {

        enqueueList(trackQueue, trackList, trackTime);
    }

    public void enqueuePricing(List<String> pricingList) {

        enqueueList(pricingQueue, pricingList, pricingTime);
    }

    @Scheduled(fixedRate = 100)
    public void dequeueShipments() {

        boolean isTime = queueTime > 0 && shipmentsTime.isTime();

        List<String> shipmentsList = new ArrayList<>();
        if (shipmentsQueue.size() == queueCapacity || (isTime && shipmentsQueue.size() > 0)) {
            shipmentsQueue.drainTo(shipmentsList, queueCapacity);
            applicationEventPublisher.publishEvent(new ThrottledQueueEvent(this, "shipments", shipmentsList));
            shipmentsList.clear();
        }
    }

    @Scheduled(fixedRate = 100)
    public void dequeueTrack() {

        boolean isTime = queueTime > 0 && shipmentsTime.isTime();

        List<String> trackList = new ArrayList<>();
        if (trackQueue.size() == queueCapacity || (isTime && trackQueue.size() > 0)) {
            trackQueue.drainTo(trackList, queueCapacity);
            applicationEventPublisher.publishEvent(new ThrottledQueueEvent(this, "track", trackList));
            trackList.clear();
        }
    }

    @Scheduled(fixedRate = 100)
    public void dequeuePricing() {

        boolean isTime = queueTime > 0 && shipmentsTime.isTime();

        List<String> pricingList = new ArrayList<>();
        if (pricingQueue.size() == queueCapacity || (isTime && pricingQueue.size() > 0)) {
            pricingQueue.drainTo(pricingList, queueCapacity);
            applicationEventPublisher.publishEvent(new ThrottledQueueEvent(this, "pricing", pricingList));
            pricingList.clear();
        }
    }
}
