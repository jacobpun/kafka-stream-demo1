package com.pk.kafkastreamdemo;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        var truckNumbers = List.of("trk1", "trk2", "trk3", "trk4", "trk5");
        var locations = List.of("US", "EU");
        Runnable r = () -> {
            var locEvent = new TruckLocationEvent(
                locations.get(new Random().nextInt(locations.size())),
                truckNumbers.get(new Random().nextInt(truckNumbers.size())),
                new Random().nextLong(), 
                new Random().nextLong()
            );
            var message = MessageBuilder
                            .withPayload(locEvent)
                            .setHeader(MESSAGE_KEY, locEvent.getLocation().getBytes())
                            .build();
            try {
                truckLocationOut.send(message);
                log.info("Sent: " + locEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(r, 1, 1, TimeUnit.SECONDS);
    }
}