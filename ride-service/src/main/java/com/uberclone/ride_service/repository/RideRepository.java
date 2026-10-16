package com.uberclone.ride_service.repository;

import com.uberclone.ride_service.entity.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RideRepository extends MongoRepository<Ride, String> {
    List<Ride> findByUserId(String userId);
    List<Ride> findByDriverId(String driverId);
    List<Ride> findByStatus(Ride.RideStatus status);

    List<Ride> findByStatusAndBookedAtBefore(
            Ride.RideStatus status,
            LocalDateTime time
    );
}