package org.example.monitoringservice.monitoringservice.Entities;

public record MailDTO(
    String subject,
    String body,
    boolean async,
    String... recipients
) {
}
