package com.pk.kafkastreamdemo.serde;

import com.pk.kafkastreamdemo.model.AssignDriverEvent;

import org.springframework.kafka.support.serializer.JsonSerde;

public class AssignDriverEventSerde extends JsonSerde<AssignDriverEvent>  {}