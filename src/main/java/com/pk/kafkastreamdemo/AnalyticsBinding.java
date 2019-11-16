package com.pk.kafkastreamdemo;

import com.pk.kafkastreamdemo.model.TruckLocationEvent;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface AnalyticsBinding {
    String TRUCK_LOCATION_OUT = "tlout";
    String TRUCK_LOCATION_IN = "tlin";
	String US_TRUCK_COUNT_MV = "tcmv";
    String US_TRUCK_EVENT_COUNT_OUT = "ustout";
	String US_TRUCK_EVENT_COUNT_IN = "ustin";
    
    @Input(TRUCK_LOCATION_IN)
    KStream<String, TruckLocationEvent> truckLocationIn();

    @Output(TRUCK_LOCATION_OUT)
    MessageChannel truckLocationOut();

    @Input(US_TRUCK_EVENT_COUNT_IN)
    KTable<String, Long> usTruckCountIn();

    @Output(US_TRUCK_EVENT_COUNT_OUT)
    KStream<String, Long> usTruckCountOut();
}