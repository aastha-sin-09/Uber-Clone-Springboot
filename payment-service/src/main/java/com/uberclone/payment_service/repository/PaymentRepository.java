package com.uberclone.payment_service.repository;

import com.uberclone.payment_service.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByRideId(String rideId);
    List<Payment> findByUserId(String userId);
}