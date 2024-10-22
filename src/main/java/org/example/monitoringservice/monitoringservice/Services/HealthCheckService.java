package org.example.monitoringservice.monitoringservice.Services;


import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.monitoringservice.Entities.HealthResponse;
import org.example.monitoringservice.monitoringservice.Entities.MonitoredService;
import org.example.monitoringservice.monitoringservice.Repositories.MonitoredServiceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private final MonitoredServiceRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final MailService mailService;

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
                // Calculamos si ha pasado suficiente tiempo desde la última verificación
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

        // Si nunca se ha chequeado o ha pasado más tiempo que la frecuencia definida, se debe chequear
        return (lastChecked == null) ||
                (lastChecked.plusSeconds(service.getFrequency()).isBefore(now));
    }

    // Realizar la verificación de salud del servicio
    private void checkServiceHealth(MonitoredService service, LocalDateTime now) {
        try {
            // Realizar la llamada al endpoint de salud y mapear la respuesta al objeto HealthResponse
            HealthResponse response = restTemplate.getForObject(service.getEndpoint(), HealthResponse.class);

            // Verificar que la respuesta no sea nula y el estado del servicio sea "UP"
            if (response != null && "UP".equals(response.getStatus())
                    && response.getChecks() != null
                    && "READY".equals(response.getChecks().getReadiness().getStatus())
                    && "ALIVE".equals(response.getChecks().getLiveness().getStatus())) {

                service.setHealthy(true);  // El servicio está "healthy" si todo es correcto
            } else {
                // Si la respuesta no es "UP", el servicio se considera "down"
                service.setHealthy(false);
                mailService.sendAlert("Alerta de servicio caído",
                        "El servicio " + service.getName() + " no está respondiendo correctamente o no está UP.",
                        service.getNotificationEmails().toArray(new String[0]));
            }

        } catch (Exception e) {
            // Si ocurre una excepción, marcamos el servicio como "down" y enviamos un correo de alerta
            service.setHealthy(false);
            mailService.sendAlert("Alerta de servicio caído",
                    "El servicio " + service.getName() + " no está respondiendo correctamente.",
                    service.getNotificationEmails().toArray(new String[0]));
        }

        // Actualizar la última vez que se verificó este servicio
        service.setLastChecked(now);
        repository.save(service);  // Guardamos el nuevo estado en la base de datos
    }


}

