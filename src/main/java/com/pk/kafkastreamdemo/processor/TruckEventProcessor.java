package com.pk.kafkastreamdemo.processor;

import com.pk.kafkastreamdemo.model.TruckDashboard;
import com.pk.kafkastreamdemo.model.TruckLocationEvent;
import com.pk.kafkastreamdemo.model.AssignDriverEvent;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.processor.StateStore;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.pk.kafkastreamdemo.serde.TruckDashboardSerde;
import org.apache.kafka.common.serialization.Serdes.StringSerde;

import static com.pk.kafkastreamdemo.AnalyticsBinding.*;

@Component
@Slf4j
public class TruckEventProcessor {
    private static Serde<TruckDashboard> VALUE_SERDE = new TruckDashboardSerde();
    private static Serde<String> KEY_SERDE = new StringSerde();
    private static String UNKNOWN_STR = "UNKNOWN"; 
    private static long UNKNOWN_NBR = -1; 

    @StreamListener
    @SendTo(TRUCK_DASHBOARD_OUT)
    public KStream<String, TruckDashboard> process(
            @Input(TRUCK_LOCATION_IN) KStream<String, TruckLocationEvent> locationStream,
            @Input(ASSIGN_DRIVER_IN) KStream<String, AssignDriverEvent> assignDriverStream) {
        KStream<String, TruckDashboard> dashboardFromLocStream = locationStream
                .peek((k,v) -> log.info("Received event {}: {}", k.toString(), v.toString()))        
                .mapValues(v -> new TruckDashboard(v.getTruckNumber(), UNKNOWN_STR, v.getLongitude(), v.getLatitude()));
        KStream<String, TruckDashboard> dashboardFromDriverStream =assignDriverStream
                .peek((k,v) -> log.info("Received event {}: {}", k.toString(), v.toString()))        
                .mapValues(v -> new TruckDashboard(v.getTruckNumber(), v.getDriver(), UNKNOWN_NBR, UNKNOWN_NBR));
        KStream<String, TruckDashboard> merged = dashboardFromLocStream.merge(dashboardFromDriverStream);
        
        return merged
                .groupByKey(Grouped.with(KEY_SERDE, VALUE_SERDE))
                .aggregate(
                        TruckDashboard::new, 
                        TruckEventProcessor::updateDashboard, 
                        Materialized.as(TRUCK_DASHBOARD_MV).withValueSerde(new JsonSerde(TruckDashboard.class))
                ).toStream()
                .peek((k,v) -> log.info("Emit event {}", v));
                
    }

    private static TruckDashboard updateDashboard(String truckNumber, TruckDashboard tmpDashboard, TruckDashboard aggDashboard) {
        String driver = tmpDashboard.getDriverName().equals(UNKNOWN_STR)? aggDashboard.getDriverName(): tmpDashboard.getDriverName();
        long latitude = tmpDashboard.getLatitude()==UNKNOWN_NBR? aggDashboard.getLatitude(): tmpDashboard.getLatitude();
        long longitude = tmpDashboard.getLongitude()==UNKNOWN_NBR? aggDashboard.getLongitude(): tmpDashboard.getLongitude();
        return new TruckDashboard(truckNumber, driver, longitude, latitude);
    }
}