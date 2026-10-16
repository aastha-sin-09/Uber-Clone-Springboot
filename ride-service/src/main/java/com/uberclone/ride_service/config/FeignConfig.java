package com.uberclone.ride_service.config;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor traceIdInterceptor() {
        return requestTemplate -> {
            String traceId = MDC.get("traceId");
            if (traceId != null) {
                // Pass traceId to downstream service
                requestTemplate.header("X-Trace-Id", traceId);
            }
        };
    }
}