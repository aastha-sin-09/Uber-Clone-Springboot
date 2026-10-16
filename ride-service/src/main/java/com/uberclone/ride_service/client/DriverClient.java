package com.uberclone.ride_service.client;

import com.uberclone.ride_service.config.FeignConfig;
import com.uberclone.ride_service.dto.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(
        name = "driver-service",
        fallback = DriverClientFallback.class ,   // ← fallback when driver-service is down
        configuration = FeignConfig.class
)
public interface DriverClient {

    @GetMapping("/api/drivers/available")
    List<DriverResponse> getAvailableDrivers();

    @PutMapping("/api/drivers/availability")
    DriverResponse updateAvailability(
            @RequestParam String driverId,
            @RequestParam boolean available
    );
}