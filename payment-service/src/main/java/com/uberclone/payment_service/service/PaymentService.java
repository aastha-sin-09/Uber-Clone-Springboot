package com.uberclone.payment_service.service;

import com.uberclone.payment_service.dto.PaymentRequest;
import com.uberclone.payment_service.dto.PaymentResponse;
import com.uberclone.payment_service.entity.Payment;
import com.uberclone.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentResponse processPayment(PaymentRequest request) {

        // Check if payment already exists for this ride
        if (paymentRepository.findByRideId(request.getRideId()).isPresent()) {
            throw new RuntimeException("Payment already processed for ride: "
                    + request.getRideId());
        }

        // Simulate payment gateway call
        boolean paymentSuccess = simulatePaymentGateway(request.getPaymentMethod());

        Payment.PaymentStatus status = paymentSuccess
                ? Payment.PaymentStatus.SUCCESS
                : Payment.PaymentStatus.FAILED;

        // Generate simulated transaction ID
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
                .rideId(request.getRideId())
                .userId(request.getUserId())
                .driverId(request.getDriverId())
                .amount(request.getAmount())
                .status(status)
                .paymentMethod(Payment.PaymentMethod.valueOf(
                        request.getPaymentMethod() != null
                                ? request.getPaymentMethod() : "CASH"))
                .transactionId(transactionId)
                .createdAt(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);

        log.info("Payment {} processed for ride {} - Status: {}",
                transactionId, request.getRideId(), status);

        return new PaymentResponse(
                saved.getId(),
                transactionId,
                status.name(),
                request.getAmount(),
                paymentSuccess ? "Payment successful" : "Payment failed"
        );
    }

    public Payment getPaymentByRide(String rideId) {
        return paymentRepository.findByRideId(rideId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment not found for ride: " + rideId));
    }

    public List<Payment> getPaymentsByUser(String userId) {
        return paymentRepository.findByUserId(userId);
    }

    // Simulates a payment gateway — 90% success rate
    // In production: replace with Razorpay/Stripe SDK call
    private boolean simulatePaymentGateway(String method) {
        if ("CASH".equals(method)) return true;   // cash always succeeds
        return Math.random() > 0.1;               // 90% success for digital
    }
}