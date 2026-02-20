package com.example.health;

import com.example.service.ItemService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

/**
 * Custom readiness health check.
 * Verifies that the application is ready to serve requests.
 */
@Readiness
@ApplicationScoped
public class ItemServiceHealthCheck implements HealthCheck {

    @Inject
    ItemService itemService;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named("Item Service");
        
        try {
            long count = itemService.count();
            builder.up()
                    .withData("itemCount", count)
                    .withData("status", "Service is operational");
        } catch (Exception e) {
            builder.down()
                    .withData("error", e.getMessage());
        }
        
        return builder.build();
    }
}
