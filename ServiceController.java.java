package com.kkkarwash.controller;

import com.kkkarwash.model.Service;
import com.kkkarwash.service.ServiceService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.List;

@Controller("/api/services")
public class ServiceController {
    
    private final ServiceService serviceService;
    
    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }
    
    @Get
    public HttpResponse<List<Service>> getAllServices() {
        return HttpResponse.ok(serviceService.getAllActiveServices());
    }
    
    @Get("/{id}")
    public HttpResponse<Service> getService(@PathVariable Long id) {
        return HttpResponse.ok(serviceService.getServiceById(id));
    }
    
    @Post
    @Secured({"admin"})
    public HttpResponse<Service> createService(@Body Service service) {
        return HttpResponse.created(serviceService.createService(service));
    }
    
    @Put("/{id}")
    @Secured({"admin"})
    public HttpResponse<Service> updateService(@PathVariable Long id, @Body Service service) {
        return HttpResponse.ok(serviceService.updateService(id, service));
    }
    
    @Delete("/{id}")
    @Secured({"admin"})
    public HttpResponse<?> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return HttpResponse.ok();
    }
    
    @Put("/{id}/toggle-active")
    @Secured({"admin"})
    public HttpResponse<Service> toggleServiceActive(@PathVariable Long id) {
        return HttpResponse.ok(serviceService.toggleServiceActive(id));
    }
}