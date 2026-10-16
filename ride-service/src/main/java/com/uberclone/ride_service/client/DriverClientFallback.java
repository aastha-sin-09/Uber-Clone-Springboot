package com.uberclone.ride_service.client;

import com.uberclone.ride_service.dto.DriverResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class DriverClientFallback implements DriverClient {

    @Override
    public List<DriverResponse> getAvailableDrivers() {
        log.warn("Driver Service is DOWN — returning empty driver list (fallback)");
        return Collections.emptyList();   // empty list triggers "No drivers available"
    }

    @Override
    public DriverResponse updateAvailability(String driverId, boolean available) {
        log.warn("Driver Service is DOWN — could not update availability for driver: {}",
                driverId);
        return new DriverResponse();      // return empty response gracefully
    }
}