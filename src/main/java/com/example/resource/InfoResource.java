package com.example.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.Instant;
import java.util.Map;

/**
 * Application info endpoint.
 * Useful for debugging and verification in Kubernetes.
 */
@Path("/api/info")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Info", description = "Application information")
public class InfoResource {

    private static final Instant START_TIME = Instant.now();

    @GET
    @Operation(summary = "Get application info", description = "Returns application metadata and runtime info")
    public Map<String, Object> getInfo() {
        return Map.of(
                "application", "quarkus-k3s-poc",
                "version", "1.0.0",
                "framework", "Quarkus",
                "startTime", START_TIME.toString(),
                "uptime", java.time.Duration.between(START_TIME, Instant.now()).toSeconds() + " seconds",
                "javaVersion", System.getProperty("java.version"),
                "hostname", getHostname(),
                "podName", System.getenv().getOrDefault("HOSTNAME", "unknown"),
                "timestamp", Instant.now().toString()
        );
    }

    @GET
    @Path("/env")
    @Operation(summary = "Get environment info", description = "Returns selected environment variables (non-sensitive)")
    public Map<String, String> getEnvironment() {
        return Map.of(
                "HOSTNAME", System.getenv().getOrDefault("HOSTNAME", "unknown"),
                "KUBERNETES_SERVICE_HOST", System.getenv().getOrDefault("KUBERNETES_SERVICE_HOST", "not-in-k8s"),
                "JAVA_OPTS", System.getenv().getOrDefault("JAVA_OPTS", "default"),
                "TZ", System.getenv().getOrDefault("TZ", "UTC")
        );
    }

    private String getHostname() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
