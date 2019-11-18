package com.pk.kafkastreamdemo.serde;

import com.pk.kafkastreamdemo.model.TruckDashboard;

import org.springframework.kafka.support.serializer.JsonSerde;

public class TruckDashboardSerde extends JsonSerde<TruckDashboard> {
}