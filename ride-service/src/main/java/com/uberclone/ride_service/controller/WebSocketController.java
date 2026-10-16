package com.uberclone.ride_service.controller;

import com.uberclone.ride_service.dto.RideStatusUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    // Client sends message to /app/ride-update
    // Server broadcasts to /topic/ride/{rideId}
    @MessageMapping("/ride-update")
    @SendTo("/topic/rides")
    public RideStatusUpdate handleRideUpdate(RideStatusUpdate update) {
        log.info("WebSocket: Ride update received for ride: {}", update.getRideId());
        return update;
    }

    // Called internally when ride status changes
    public void sendRideUpdate(String rideId, RideStatusUpdate update) {
        log.info("WebSocket: Pushing update for ride {} — Status: {}",
                rideId, update.getStatus());
        messagingTemplate.convertAndSend(
                "/topic/ride/" + rideId,   // each ride has its own topic
                update
        );
    }
}