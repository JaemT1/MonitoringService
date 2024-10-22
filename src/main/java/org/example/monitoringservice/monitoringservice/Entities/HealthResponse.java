package org.example.monitoringservice.monitoringservice.Entities;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HealthResponse {

    private String status;
    private Checks checks;

    @Data
    public static class Checks {
        private CheckDetail readiness;
        private CheckDetail liveness;
    }

    @Data
    public static class CheckDetail {
        private LocalDateTime from;
        private String status;
    }
}
