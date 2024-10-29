package org.example.monitoringservice.monitoringservice.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class MonitoredService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique=true, nullable=false)
    private String name;

    @NotBlank
    @Column(unique=true, nullable=false)
    private String endpoint;

    @NotNull
    private Integer frequency; // Frecuencia de monitoreo en segundos

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "service_emails", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "email")
    private List<String> notificationEmails;

    private boolean healthy = true;  // Estado actual del servicio (UP/DOWN)

    private LocalDateTime lastChecked; // Última vez que se verificó el estado del servicio
}
