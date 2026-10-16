package com.uberclone.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String paymentId;
    private String transactionId;
    private String status;
    private double amount;
    private String message;
}