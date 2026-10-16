package com.uberclone.payment_service.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    private String rideId;

    private String userId;

    private String driverId;

    private double amount;

    private PaymentStatus status;

    private PaymentMethod paymentMethod;

    private String transactionId;      // simulated transaction ID

    private LocalDateTime createdAt;

    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED
    }

    public enum PaymentMethod {
        CASH,
        UPI,
        CARD
    }
}