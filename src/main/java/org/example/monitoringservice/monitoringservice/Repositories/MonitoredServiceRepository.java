package org.example.monitoringservice.monitoringservice.Repositories;

import org.example.monitoringservice.monitoringservice.Entities.MonitoredService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonitoredServiceRepository extends JpaRepository<MonitoredService, Long> {
    Optional<MonitoredService> findByName(String name);
}
