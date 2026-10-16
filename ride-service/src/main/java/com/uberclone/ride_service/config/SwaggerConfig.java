package com.uberclone.ride_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI rideServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("URide — Ride Service API")
                        .description("Handles ride booking, tracking, surge pricing and scheduling")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Aastha")
                                .email("aastha@gmail.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8083")
                                .description("Local Development Server")
                ));
    }
}