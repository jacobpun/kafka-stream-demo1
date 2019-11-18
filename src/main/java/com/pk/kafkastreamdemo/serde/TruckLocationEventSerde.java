package com.pk.kafkastreamdemo.serde;

import org.springframework.kafka.support.serializer.JsonSerde;
import com.pk.kafkastreamdemo.model.TruckLocationEvent;


public class TruckLocationEventSerde extends JsonSerde<TruckLocationEvent> {}