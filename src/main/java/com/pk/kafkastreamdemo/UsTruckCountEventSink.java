package com.pk.kafkastreamdemo;

import org.apache.kafka.streams.kstream.KTable;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UsTruckCountEventSink {
    @StreamListener
    public void process(@Input(AnalyticsBinding.US_TRUCK_EVENT_COUNT_IN) KTable<String, Long> table){
        table.toStream().foreach((k, v) -> log.info("SINK --- " + k + ": " + v));
    }
}