package com.uberclone.notification_service.kafka;

import com.uberclone.notification_service.dto.RideCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    @KafkaListener(
            topics = "ride-completed",
            groupId = "notification-group"
    )
    public void handleRideCompleted(RideCompletedEvent event) {

        log.info("======================================");
        log.info("NOTIFICATION SERVICE TRIGGERED");
        log.info("Ride ID    : {}", event.getRideId());
        log.info("User ID    : {}", event.getUserId());
        log.info("From       : {}", event.getPickupAddress());
        log.info("To         : {}", event.getDropAddress());
        log.info("Fare       : ₹{}", event.getFare());
        log.info("======================================");

        // Simulate sending email/SMS
        sendRideReceiptNotification(event);
    }

    private void sendRideReceiptNotification(RideCompletedEvent event) {
        // In production: integrate with SendGrid/Twilio here
        log.info("📧 Sending ride receipt to user: {}", event.getUserId());
        log.info("📱 Sending SMS to driver: {}", event.getDriverId());
        log.info("✅ Notifications sent successfully!");
    }
}