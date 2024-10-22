package org.example.monitoringservice.monitoringservice.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.monitoringservice.Entities.ApiError;
import org.example.monitoringservice.monitoringservice.Entities.ApiSuccess;
import org.example.monitoringservice.monitoringservice.Entities.MonitoredService;
import org.example.monitoringservice.monitoringservice.Entities.MonitoredServiceDTO;
import org.example.monitoringservice.monitoringservice.Services.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private HealthCheckService healthCheckService;

    // Registrar un nuevo microservicio para monitoreo
    @PostMapping("/register")
    public ResponseEntity<?> registerService(@Validated @RequestBody MonitoredServiceDTO serviceDTO, HttpServletRequest request) {
        try {
            MonitoredService service = new MonitoredService();
            service.setName(serviceDTO.getName());
            service.setEndpoint(serviceDTO.getEndpoint());
            service.setFrequency(serviceDTO.getFrequency());
            service.setNotificationEmails(serviceDTO.getNotificationEmails());
            service.setHealthy(true);
            service.setLastChecked(LocalDateTime.now());

            MonitoredService savedService = healthCheckService.registerService(service);

            ApiSuccess apiSuccess = new ApiSuccess(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    "Servicio añadido con exito",
                    request.getRequestURI()
            );

            return ResponseEntity.ok(apiSuccess);
        } catch (Exception e) {
            ApiError apiError = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    "Error al añadir el servicio",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
        }
    }

    // Obtener el estado de salud de todos los microservicios registrados
    @GetMapping
    public ResponseEntity<List<MonitoredService>> getAllServicesHealth() {
        return ResponseEntity.ok(healthCheckService.getAllServices());
    }

    // Obtener el estado de salud de un microservicio específico
    @GetMapping("/{name}")
    public ResponseEntity<MonitoredService> getServiceHealth(@PathVariable String name) {
        Optional<MonitoredService> service = healthCheckService.getServiceByName(name);
        return service.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
