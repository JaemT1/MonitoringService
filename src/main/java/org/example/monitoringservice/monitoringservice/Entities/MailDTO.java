package org.example.monitoringservice.monitoringservice.Entities;

public record MailDTO(
    String subject,
    String body,
    String serviceName,
    String serviceEndpoint,
    boolean async,
    String... recipients
) {
}
