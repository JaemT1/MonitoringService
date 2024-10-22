package org.example.monitoringservice.monitoringservice.Entities;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MonitoredServiceDTO {

    @NotBlank(message = "El nombre del servicio es obligatorio.")
    private String name;

    @NotBlank(message = "El endpoint es obligatorio.")
    private String endpoint;

    @NotNull(message = "La frecuencia de monitoreo es obligatoria.")
    private Integer frequency; // Frecuencia de monitoreo en segundos

    @NotNull(message = "Debe proporcionar al menos un correo electrónico para las notificaciones.")
    private List<String> notificationEmails;
}

