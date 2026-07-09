package com.kkkarwash.service;

import com.kkkarwash.model.Service;
import com.kkkarwash.repository.ServiceRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;

import javax.transaction.Transactional;
import java.util.List;

@Singleton
public class ServiceService {
    
    private final ServiceRepository serviceRepository;
    
    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }
    
    public List<Service> getAllActiveServices() {
        return serviceRepository.findByIsActiveTrue();
    }
    
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
    
    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Service not found"));
    }
    
    @Transactional
    public Service createService(Service service) {
        // Validate service data
        if (service.getName() == null || service.getName().trim().isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Service name is required");
        }
        if (service.getPrice() == null || service.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Price must be greater than 0");
        }
        if (service.getDuration() == null || service.getDuration() <= 0) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Duration must be greater than 0");
        }
        
        service.setActive(true);
        return serviceRepository.save(service);
    }
    
    @Transactional
    public Service updateService(Long id, Service updatedService) {
        Service service = getServiceById(id);
        
        if (updatedService.getName() != null) {
            service.setName(updatedService.getName());
        }
        if (updatedService.getDescription() != null) {
            service.setDescription(updatedService.getDescription());
        }
        if (updatedService.getPrice() != null && updatedService.getPrice().compareTo(java.math.BigDecimal.ZERO) > 0) {
            service.setPrice(updatedService.getPrice());
        }
        if (updatedService.getDuration() != null && updatedService.getDuration() > 0) {
            service.setDuration(updatedService.getDuration());
        }
        if (updatedService.getIcon() != null) {
            service.setIcon(updatedService.getIcon());
        }
        if (updatedService.getFeatures() != null) {
            service.setFeatures(updatedService.getFeatures());
        }
        
        return serviceRepository.update(service);
    }
    
    @Transactional
    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Service not found");
        }
        
        // Soft delete by setting inactive instead of actual deletion
        Service service = getServiceById(id);
        service.setActive(false);
        serviceRepository.update(service);
    }
    
    @Transactional
    public Service toggleServiceActive(Long id) {
        Service service = getServiceById(id);
        service.setActive(!service.isActive());
        return serviceRepository.update(service);
    }
    
    public long getActiveServiceCount() {
        return serviceRepository.countByIsActiveTrue();
    }
    
    public Service getServiceByName(String name) {
        return serviceRepository.findAll().stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}