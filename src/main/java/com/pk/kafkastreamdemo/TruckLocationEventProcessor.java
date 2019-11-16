package com.pk.kafkastreamdemo;

import com.pk.kafkastreamdemo.model.TruckLocationEvent;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class TruckLocationEventProcessor {
    @StreamListener
    @SendTo(AnalyticsBinding.US_TRUCK_EVENT_COUNT_OUT)
    public KStream<String, Long> process(@Input(AnalyticsBinding.TRUCK_LOCATION_IN) KStream<String, TruckLocationEvent> stream) {
        return stream
            .filter((k, v) -> k.equals("US"))
            .map((k, v) -> KeyValue.pair(v.getTruckNumber(), "-"))
            .groupByKey()
            .count(Materialized.as(AnalyticsBinding.US_TRUCK_COUNT_MV))
            .toStream();
    }
}