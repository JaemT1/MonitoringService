package org.example.monitoringservice.monitoringservice.Services;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.monitoringservice.Entities.HealthResponse;
import org.example.monitoringservice.monitoringservice.Entities.MailDTO;
import org.example.monitoringservice.monitoringservice.Entities.MonitoredService;
import org.example.monitoringservice.monitoringservice.Repositories.MonitoredServiceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private final MonitoredServiceRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    // URL del microservicio de correo
    private static final String MAIL_SERVICE_URL = "http://localhost:8004/mail/enviar";

    // Registrar un nuevo microservicio
    public MonitoredService registerService(MonitoredService service) {
        return repository.save(service);
    }

    // Obtener todos los microservicios registrados
    public List<MonitoredService> getAllServices() {
        return repository.findAll();
    }

    // Obtener un microservicio específico por nombre
    public Optional<MonitoredService> getServiceByName(String name) {
        return repository.findByName(name);
    }

    // Monitorear todos los servicios cada 60 segundos
    @Scheduled(fixedRate = 60000)
    public void monitorServices() {
        try {
            List<MonitoredService> services = repository.findAll();
            LocalDateTime now = LocalDateTime.now();

            for (MonitoredService serviceAux : services) {
                if (shouldCheckService(serviceAux, now)) {
                    checkServiceHealth(serviceAux, now);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al monitorear los servicios: " + e.getMessage());
        }
    }

    // Verificar si un microservicio debe ser chequeado
    private boolean shouldCheckService(MonitoredService service, LocalDateTime now) {
        LocalDateTime lastChecked = service.getLastChecked();
        return (lastChecked == null) || (lastChecked.plusSeconds(service.getFrequency()).isBefore(now));
    }

    // Realizar la verificación de salud del servicio
    private void checkServiceHealth(MonitoredService service, LocalDateTime now) {
        try {
            HealthResponse response = restTemplate.getForObject(service.getEndpoint(), HealthResponse.class);

            if (response != null && "UP".equals(response.getStatus())
                    && response.getChecks() != null
                    && "READY".equals(response.getChecks().getReadiness().getStatus())
                    && "ALIVE".equals(response.getChecks().getLiveness().getStatus())) {

                service.setHealthy(true);  // El servicio está "healthy"
            } else {
                service.setHealthy(false);  // El servicio está "down"
                sendAlertToMailService(service);
            }
        } catch (Exception e) {
            service.setHealthy(false);  // Excepción: El servicio está "down"
            sendAlertToMailService(service);
        }

        service.setLastChecked(now);
        repository.save(service);
    }

    // Método para enviar la alerta al microservicio de correo
    private void sendAlertToMailService(MonitoredService service) {
        try {
            // Construir el DTO del correo
            MailDTO mailDTO = new MailDTO(
                    "Alerta de servicio caído",
                    "El servicio " + service.getName() + " no está respondiendo correctamente.",
                    service.getName(),
                    service.getEndpoint(),
                    true,
                    service.getNotificationEmails().toArray(new String[0])
            );

            // Configurar las cabeceras (headers)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Crear la petición HTTP con el cuerpo y headers
            HttpEntity<MailDTO> request = new HttpEntity<>(mailDTO, headers);

            // Hacer la petición POST al microservicio de correo
            ResponseEntity<String> response = restTemplate.exchange(
                    MAIL_SERVICE_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Error al enviar alerta de correo: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Error al enviar alerta de correo: " + e.getMessage());
        }
    }
}
