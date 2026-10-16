package com.uberclone.ride_service.kafka;

import com.uberclone.ride_service.dto.RideCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RideEventProducer {

    private final KafkaTemplate<String, RideCompletedEvent> kafkaTemplate;

    private static final String TOPIC = "ride-completed";

    public void publishRideCompleted(RideCompletedEvent event) {
        kafkaTemplate.send(TOPIC, event.getRideId(), event);
        log.info("Published ride-completed event for ride: {}", event.getRideId());
    }
}