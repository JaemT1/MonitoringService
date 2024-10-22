package org.example.monitoringservice.monitoringservice.Entities;

public record MailDTO(
    String subject,
    String body,
    String... recipients
) {
}
