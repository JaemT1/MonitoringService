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
    public ResponseEntity<?> registerService(@RequestBody MonitoredServiceDTO serviceDTO, HttpServletRequest request) {
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
                    HttpStatus.CREATED.value(),
                    HttpStatus.CREATED.getReasonPhrase(),
                    "Servicio añadido con éxito",
                    request.getRequestURI()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(apiSuccess);
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
    public ResponseEntity<?> getAllServicesHealth(HttpServletRequest request) {
        try {
            List<MonitoredService> services = healthCheckService.getAllServices();
            if (services.isEmpty()) {
                throw new Exception("No se encontraron servicios");
            }
            ApiSuccess apiSuccess = new ApiSuccess(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    "Servicios obtenidos con éxito",
                    request.getRequestURI(),
                    services
            );
            return ResponseEntity.ok(apiSuccess);
        } catch (Exception e) {
            ApiError apiError = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    "Error al obtener los servicios",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
        }
    }

    // Obtener el estado de salud de un microservicio específico
    @GetMapping("/{name}")
    public ResponseEntity<?> getServiceHealth(@PathVariable String name, HttpServletRequest request) {
        try {
            Optional<MonitoredService> service = healthCheckService.getServiceByName(name);
            if (service.isPresent()) {
                ApiSuccess apiSuccess = new ApiSuccess(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.getReasonPhrase(),
                        "Servicio encontrado",
                        request.getRequestURI(),
                        service
                );
                return ResponseEntity.ok(apiSuccess);
            } else {
                ApiError apiError = new ApiError(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "Servicio no encontrado",
                        request.getRequestURI()
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
            }
        } catch (Exception e) {
            ApiError apiError = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    "Error al obtener el servicio",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
        }
    }

    // Eliminar un microservicio por ID
    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteServiceById(@PathVariable String name, HttpServletRequest request) {
        try {
            boolean deleted = healthCheckService.deleteServiceByName(name);
            if (deleted) {
                ApiSuccess apiSuccess = new ApiSuccess(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.getReasonPhrase(),
                        "Servicio eliminado con éxito",
                        request.getRequestURI()
                );
                return ResponseEntity.ok(apiSuccess);
            } else {
                ApiError apiError = new ApiError(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "Servicio no encontrado",
                        request.getRequestURI()
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
            }
        } catch (Exception e) {
            ApiError apiError = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    "Error al eliminar el servicio",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
        }
    }

    // Eliminar todos los microservicios
    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllServices(HttpServletRequest request) {
        try {
            healthCheckService.deleteAllServices();
            ApiSuccess apiSuccess = new ApiSuccess(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    "Todos los servicios eliminados con éxito",
                    request.getRequestURI()
            );
            return ResponseEntity.ok(apiSuccess);
        } catch (Exception e) {
            ApiError apiError = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    "Error al eliminar todos los servicios",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
        }
    }
}
