package com.uberclone.payment_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotBlank
    private String rideId;

    @NotBlank
    private String userId;

    @NotBlank
    private String driverId;

    private double amount;

    private String paymentMethod;   // "CASH", "UPI", "CARD"
}