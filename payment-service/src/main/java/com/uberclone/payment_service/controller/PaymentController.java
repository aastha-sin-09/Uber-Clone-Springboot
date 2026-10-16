package com.uberclone.payment_service.controller;

import com.uberclone.payment_service.dto.PaymentRequest;
import com.uberclone.payment_service.dto.PaymentResponse;
import com.uberclone.payment_service.entity.Payment;
import com.uberclone.payment_service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Controller", description = "Payment processing and history")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @Operation(summary = "Process payment",
            description = "Simulates payment gateway with 90% success rate for digital payments")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @GetMapping("/ride/{rideId}")
    @Operation(summary = "Get payment by ride ID")
    public ResponseEntity<Payment> getPaymentByRide(@PathVariable String rideId) {
        return ResponseEntity.ok(paymentService.getPaymentByRide(rideId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get payment history for user")
    public ResponseEntity<List<Payment>> getPaymentsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUser(userId));
    }
}