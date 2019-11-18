package com.pk.kafkastreamdemo.service;

import com.pk.kafkastreamdemo.AnalyticsBinding;
import com.pk.kafkastreamdemo.model.AssignDriverEvent;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.kafka.support.KafkaHeaders.MESSAGE_KEY;

@Service
@Slf4j
public class TruckService {
    private final MessageChannel assignDriverOut;

    public TruckService(AnalyticsBinding binding) {
        this.assignDriverOut = binding.assignDriverOut();
    }

    public void assignDriver(final AssignDriverEvent req) {
        try {
            var message = MessageBuilder.withPayload(req)
            .setHeader(MESSAGE_KEY, req.getTruckNumber().getBytes())
            //.setHeader(headerName, headerValue)
            .build();

            try {
                assignDriverOut.send(message);
                log.info("Sent Driver assign event: " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}