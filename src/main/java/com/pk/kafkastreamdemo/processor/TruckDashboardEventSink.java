package com.pk.kafkastreamdemo.processor;

import com.pk.kafkastreamdemo.AnalyticsBinding;
import com.pk.kafkastreamdemo.model.TruckDashboard;

import org.apache.kafka.streams.kstream.KTable;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TruckDashboardEventSink {
    @StreamListener
    public void process(@Input(AnalyticsBinding.TRUCK_DASHBOARD_IN) KTable<String, TruckDashboard> table){
        table.toStream().foreach((k, v) -> log.info("SINK --- {}: {}", k, v));
    }
}