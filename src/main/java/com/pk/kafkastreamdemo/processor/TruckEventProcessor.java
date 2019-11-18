package com.pk.kafkastreamdemo.processor;

import com.pk.kafkastreamdemo.model.TruckDashboard;
import com.pk.kafkastreamdemo.model.TruckLocationEvent;
import com.pk.kafkastreamdemo.model.AssignDriverEvent;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
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
            @Input(ASSIGN_DRIVER_IN) KStream<String, AssignDriverEvent> assignDriverStream,
            @Input(TRUCK_DASHBOARD_IN) KTable<String, TruckDashboard> dashboardTable) {
        KStream<String, TruckDashboard> dashboardFronLoc = locationStream
                .peek((k,v) -> log.info("Received event {}: {}", k.toString(), v.toString()))        
                .mapValues(v -> new TruckDashboard(v.getTruckNumber(), UNKNOWN_STR, v.getLongitude(), v.getLatitude()));
        KStream<String, TruckDashboard> dashboardFromDriver =assignDriverStream
                .peek((k,v) -> log.info("Received event {}: {}", k.toString(), v.toString()))        
                .mapValues(v -> new TruckDashboard(v.getTruckNumber(), v.getDriver(), UNKNOWN_NBR, UNKNOWN_NBR));
        KStream<String, TruckDashboard> merged = dashboardFronLoc.merge(dashboardFromDriver);
        return merged.leftJoin(dashboardTable, this::updateDashboard)
                .groupByKey(Grouped.with(KEY_SERDE, VALUE_SERDE))
                .reduce((agg, v) -> v, Materialized.as(TRUCK_DASHBOARD_MV))
                .toStream()
                .peek((k,v) -> log.info("Emit event {}", v));
    }

    private TruckDashboard updateDashboard(TruckDashboard tmpDashboard, TruckDashboard aggDashboard) {
        if (aggDashboard == null) {
                aggDashboard = new TruckDashboard();
                aggDashboard.setTruckNumber(tmpDashboard.getTruckNumber());
        }
        
        String driver = tmpDashboard.getDriverName().equals(UNKNOWN_STR)? aggDashboard.getDriverName(): tmpDashboard.getDriverName();
        long latitude = tmpDashboard.getLatitude()==UNKNOWN_NBR? aggDashboard.getLatitude(): tmpDashboard.getLatitude();
        long longitude = tmpDashboard.getLongitude()==UNKNOWN_NBR? aggDashboard.getLongitude(): tmpDashboard.getLongitude();
        return new TruckDashboard(aggDashboard.getTruckNumber(), driver, longitude, latitude);
    }
}