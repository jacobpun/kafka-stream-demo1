package com.pk.kafkastreamdemo.source;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.pk.kafkastreamdemo.AnalyticsBinding;
import com.pk.kafkastreamdemo.model.TruckLocationEvent;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import static org.springframework.kafka.support.KafkaHeaders.MESSAGE_KEY;

@Component
@Slf4j
public class TruckLocationEventSource implements ApplicationRunner {
    private final MessageChannel truckLocationOut;

    TruckLocationEventSource(AnalyticsBinding binding) {
        this.truckLocationOut = binding.truckLocationOut();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var random = new Random();
        Runnable r = () -> {
            String truckNumber = MockData.TRUCK_NUMBERS.get(random.nextInt(MockData.TRUCK_NUMBERS.size()));
            var locnEvent = new TruckLocationEvent(
                new Date(),
                truckNumber,
                random.nextLong(), 
                random.nextLong()
            );
            var message = MessageBuilder
                            .withPayload(locnEvent)
                            .setHeader(MESSAGE_KEY, locnEvent.getTruckNumber().getBytes())
                            .build();
            try {
                truckLocationOut.send(message);
                log.info("Sent: " + locnEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(r, 20, 20, TimeUnit.SECONDS);
    }
}