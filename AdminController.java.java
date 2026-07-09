package com.kkkarwash.controller;

import com.kkkarwash.model.Booking;
import com.kkkarwash.model.Service;
import com.kkkarwash.model.User;
import com.kkkarwash.service.AdminService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller("/admin")
@Secured("admin")
public class AdminController {
    
    private final AdminService adminService;
    
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    // Dashboard
    @Get("/stats")
    public HttpResponse<Map<String, Object>> getStats() {
        return HttpResponse.ok(adminService.getDashboardStats());
    }
    
    @Get("/weekly-bookings")
    public HttpResponse<List<Map<String, Object>>> getWeeklyBookings() {
        return HttpResponse.ok(adminService.getWeeklyBookings());
    }
    
    @Get("/service-popularity")
    public HttpResponse<List<Map<String, Object>>> getServicePopularity() {
        return HttpResponse.ok(adminService.getServicePopularity());
    }
    
    @Get("/recent-bookings")
    public HttpResponse<List<Booking>> getRecentBookings() {
        return HttpResponse.ok(adminService.getRecentBookings());
    }
    
    // Bookings Management
    @Get("/bookings")
    public HttpResponse<List<Booking>> getAllBookings(
        @QueryValue(required = false) String search,
        @QueryValue(required = false) String status,
        @QueryValue(required = false) String dateFrom,
        @QueryValue(required = false) String dateTo) {
        return HttpResponse.ok(adminService.getAllBookings(search, status, dateFrom, dateTo));
    }
    
    @Put("/bookings/{id}")
    public HttpResponse<Booking> updateBooking(@PathVariable Long id, @Body Booking booking) {
        return HttpResponse.ok(adminService.updateBooking(id, booking));
    }
    
    @Delete("/bookings/{id}")
    public HttpResponse<?> deleteBooking(@PathVariable Long id) {
        adminService.deleteBooking(id);
        return HttpResponse.ok();
    }
    
    @Post("/bookings/export")
    public HttpResponse<String> exportBookings(@Body Map<String, String> filters) {
        return HttpResponse.ok(adminService.exportBookings(filters));
    }
    
    // Services Management
    @Get("/services")
    public HttpResponse<List<Service>> getAllServices() {
        return HttpResponse.ok(adminService.getAllServices());
    }
    
    @Post("/services")
    public HttpResponse<Service> createService(@Body Service service) {
        return HttpResponse.created(adminService.createService(service));
    }
    
    @Put("/services/{id}")
    public HttpResponse<Service> updateService(@PathVariable Long id, @Body Service service) {
        return HttpResponse.ok(adminService.updateService(id, service));
    }
    
    @Delete("/services/{id}")
    public HttpResponse<?> deleteService(@PathVariable Long id) {
        adminService.deleteService(id);
        return HttpResponse.ok();
    }
    
    // Customers Management
    @Get("/customers")
    public HttpResponse<List<User>> getAllCustomers(
        @QueryValue(required = false) String search,
        @QueryValue(required = false) String status) {
        return HttpResponse.ok(adminService.getAllCustomers(search, status));
    }
    
    @Put("/customers/{id}/status")
    public HttpResponse<Map<String, Object>> toggleCustomerStatus(@PathVariable Long id) {
        return HttpResponse.ok(adminService.toggleCustomerStatus(id));
    }
    
    @Get("/customers/{id}/bookings")
    public HttpResponse<List<Booking>> getCustomerBookings(@PathVariable Long id) {
        return HttpResponse.ok(adminService.getCustomerBookings(id));
    }
    
    // Reports
    @Get("/reports/summary")
    public HttpResponse<Map<String, Object>> getReportSummary(@QueryValue(required = false) Map<String, String> filters) {
        return HttpResponse.ok(adminService.getReportSummary(filters));
    }
    
    @Get("/reports/revenue-trend")
    public HttpResponse<List<Map<String, Object>>> getRevenueTrend(@QueryValue(required = false) Map<String, String> filters) {
        return HttpResponse.ok(adminService.getRevenueTrend(filters));
    }
    
    @Get("/reports/booking-distribution")
    public HttpResponse<List<Map<String, Object>>> getBookingDistribution(@QueryValue(required = false) Map<String, String> filters) {
        return HttpResponse.ok(adminService.getBookingDistribution(filters));
    }
    
    @Get("/reports/top-services")
    public HttpResponse<List<Map<String, Object>>> getTopServices(@QueryValue(required = false) Map<String, String> filters) {
        return HttpResponse.ok(adminService.getTopServices(filters));
    }
    
    @Get("/reports/recent-activity")
    public HttpResponse<List<Map<String, Object>>> getRecentActivity(@QueryValue(required = false) Map<String, String> filters) {
        return HttpResponse.ok(adminService.getRecentActivity(filters));
    }
    
    @Get("/reports/detailed")
    public HttpResponse<List<Map<String, Object>>> getDetailedReport(@QueryValue(required = false) Map<String, String> filters) {
        return HttpResponse.ok(adminService.getDetailedReport(filters));
    }
    
    @Get("/reports/export")
    public HttpResponse<String> exportReport(@QueryValue String format, @QueryValue(required = false) Map<String, String> filters) {
        return HttpResponse.ok(adminService.exportReport(format, filters));
    }
}