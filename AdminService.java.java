package com.kkkarwash.service;

import com.kkkarwash.model.Booking;
import com.kkkarwash.model.Service;
import com.kkkarwash.model.User;
import com.kkkarwash.repository.BookingRepository;
import com.kkkarwash.repository.ServiceRepository;
import com.kkkarwash.repository.UserRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class AdminService {
    
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    
    public AdminService(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }
    
    // ====== Dashboard ======
    public Map<String, Object> getDashboardStats() {
        long totalBookings = bookingRepository.count();
        long pending = bookingRepository.countByStatus(Booking.Status.pending);
        long customers = userRepository.countByRole(User.Role.customer);
        long activeCustomers = userRepository.countByStatus(User.Status.active);
        
        // Calculate revenue (sum of all completed bookings)
        List<Booking> completed = bookingRepository.findByStatus(Booking.Status.completed);
        double revenue = completed.stream()
                .mapToDouble(b -> b.getTotal().doubleValue())
                .sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("revenue", Math.round(revenue));
        stats.put("bookings", totalBookings);
        stats.put("pending", pending);
        stats.put("customers", activeCustomers);
        
        return stats;
    }
    
    public List<Map<String, Object>> getWeeklyBookings() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(7);
        
        List<Booking> bookings = bookingRepository.findByBookingDateBetween(start, end);
        
        Map<String, List<Booking>> grouped = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getBookingDate().toString()));
        
        List<Map<String, Object>> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE");
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = start.plusDays(i);
            String day = date.format(formatter);
            List<Booking> dayBookings = grouped.getOrDefault(date.toString(), Collections.emptyList());
            
            Map<String, Object> item = new HashMap<>();
            item.put("day", day);
            item.put("count", dayBookings.size());
            result.add(item);
        }
        
        return result;
    }
    
    public List<Map<String, Object>> getServicePopularity() {
        List<Service> services = serviceRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Service service : services) {
            long count = bookingRepository.countByStatus(Booking.Status.completed);
            // For demo, generate random count
            Map<String, Object> item = new HashMap<>();
            item.put("name", service.getName());
            item.put("count", new Random().nextInt(50) + 10);
            result.add(item);
        }
        
        return result;
    }
    
    public List<Booking> getRecentBookings() {
        return bookingRepository.findTop5ByOrderByCreatedAtDesc();
    }
    
    // ====== Bookings Management ======
    public List<Booking> getAllBookings(String search, String status, String dateFrom, String dateTo) {
        List<Booking> all = bookingRepository.findAll();
        
        return all.stream()
                .filter(b -> {
                    if (search != null && !search.isEmpty()) {
                        String searchLower = search.toLowerCase();
                        return b.getUser().getName().toLowerCase().contains(searchLower) ||
                               b.getService().getName().toLowerCase().contains(searchLower);
                    }
                    return true;
                })
                .filter(b -> {
                    if (status != null && !status.isEmpty()) {
                        return b.getStatus().name().equals(status);
                    }
                    return true;
                })
                .filter(b -> {
                    if (dateFrom != null && !dateFrom.isEmpty()) {
                        return b.getBookingDate().isAfter(LocalDate.parse(dateFrom));
                    }
                    return true;
                })
                .filter(b -> {
                    if (dateTo != null && !dateTo.isEmpty()) {
                        return b.getBookingDate().isBefore(LocalDate.parse(dateTo));
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public Booking updateBooking(Long id, Booking updateData) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        
        if (updateData.getBookingDate() != null) {
            booking.setBookingDate(updateData.getBookingDate());
        }
        if (updateData.getBookingTime() != null) {
            booking.setBookingTime(updateData.getBookingTime());
        }
        if (updateData.getVehicle() != null) {
            booking.setVehicle(updateData.getVehicle());
        }
        if (updateData.getStatus() != null) {
            booking.setStatus(updateData.getStatus());
        }
        if (updateData.getTotal() != null) {
            booking.setTotal(updateData.getTotal());
        }
        
        return bookingRepository.update(booking);
    }
    
    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Booking not found");
        }
        bookingRepository.deleteById(id);
    }
    
    public String exportBookings(Map<String, String> filters) {
        List<Booking> bookings = getAllBookings(
            filters.get("search"),
            filters.get("status"),
            filters.get("dateFrom"),
            filters.get("dateTo")
        );
        
        StringBuilder csv = new StringBuilder();
        csv.append("Customer,Service,Date,Time,Vehicle,Status,Amount\n");
        
        for (Booking b : bookings) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%.2f\n",
                b.getUser().getName(),
                b.getService().getName(),
                b.getBookingDate().toString(),
                b.getBookingTime().toString(),
                b.getVehicle() != null ? b.getVehicle() : "",
                b.getStatus().name(),
                b.getTotal().doubleValue()
            ));
        }
        
        return csv.toString();
    }
    
    // ====== Services Management ======
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
    
    @Transactional
    public Service createService(Service service) {
        return serviceRepository.save(service);
    }
    
    @Transactional
    public Service updateService(Long id, Service updateData) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Service not found"));
        
        if (updateData.getName() != null) service.setName(updateData.getName());
        if (updateData.getDescription() != null) service.setDescription(updateData.getDescription());
        if (updateData.getPrice() != null) service.setPrice(updateData.getPrice());
        if (updateData.getDuration() != null) service.setDuration(updateData.getDuration());
        if (updateData.getIcon() != null) service.setIcon(updateData.getIcon());
        if (updateData.getFeatures() != null) service.setFeatures(updateData.getFeatures());
        service.setActive(updateData.isActive());
        
        return serviceRepository.update(service);
    }
    
    @Transactional
    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Service not found");
        }
        serviceRepository.deleteById(id);
    }
    
    // ====== Customers Management ======
    public List<User> getAllCustomers(String search, String status) {
        List<User> customers = userRepository.findByRole(User.Role.customer);
        
        return customers.stream()
                .filter(u -> {
                    if (search != null && !search.isEmpty()) {
                        String searchLower = search.toLowerCase();
                        return u.getName().toLowerCase().contains(searchLower) ||
                               u.getEmail().toLowerCase().contains(searchLower);
                    }
                    return true;
                })
                .filter(u -> {
                    if (status != null && !status.isEmpty()) {
                        return u.getStatus().name().equals(status);
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public Map<String, Object> toggleCustomerStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        
        if (user.getRole() == User.Role.admin) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Cannot toggle admin status");
        }
        
        User.Status newStatus = user.getStatus() == User.Status.active ? User.Status.inactive : User.Status.active;
        user.setStatus(newStatus);
        userRepository.update(user);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("newStatus", newStatus.name());
        return result;
    }
    
    public List<Booking> getCustomerBookings(Long id) {
        return bookingRepository.findByUserId(id);
    }
    
    // ====== Reports ======
    public Map<String, Object> getReportSummary(Map<String, String> filters) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRevenue", 8500 + new Random().nextInt(3000));
        summary.put("totalBookings", 300 + new Random().nextInt(100));
        summary.put("averageOrder", 35 + new Random().nextInt(15));
        summary.put("customerCount", 150 + new Random().nextInt(50));
        return summary;
    }
    
    public List<Map<String, Object>> getRevenueTrend(Map<String, String> filters) {
        List<Map<String, Object>> trend = new ArrayList<>();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        
        for (String day : days) {
            Map<String, Object> item = new HashMap<>();
            item.put("label", day);
            item.put("value", 200 + new Random().nextInt(600));
            trend.add(item);
        }
        
        return trend;
    }
    
    public List<Map<String, Object>> getBookingDistribution(Map<String, String> filters) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        String[] services = {"Basic Wash", "Premium Wash", "Deluxe Wash"};
        String[] colors = {"#38bdf8", "#34d399", "#fbbf24"};
        
        for (int i = 0; i < services.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("service", services[i]);
            item.put("count", 20 + new Random().nextInt(60));
            item.put("color", colors[i]);
            distribution.add(item);
        }
        
        return distribution;
    }
    
    public List<Map<String, Object>> getTopServices(Map<String, String> filters) {
        List<Map<String, Object>> topServices = new ArrayList<>();
        String[] services = {"Basic Wash", "Premium Wash", "Deluxe Wash"};
        
        for (int i = 0; i < services.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("service", services[i]);
            item.put("bookings", 15 + new Random().nextInt(70));
            item.put("revenue", 500 + new Random().nextInt(2000));
            topServices.add(item);
        }
        
        return topServices;
    }
    
    public List<Map<String, Object>> getRecentActivity(Map<String, String> filters) {
        List<Map<String, Object>> activities = new ArrayList<>();
        String[] actions = {"Booked", "Cancelled", "Completed", "Confirmed"};
        String[] customers = {"John Doe", "Sarah Williams", "Michael Chen", "Emma Thompson", "David Lee"};
        
        for (int i = 0; i < 8; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("action", actions[new Random().nextInt(actions.length)]);
            item.put("customer", customers[new Random().nextInt(customers.length)]);
            item.put("timestamp", String.format("%d:%02d %s", 
                8 + new Random().nextInt(6), 
                new Random().nextInt(60),
                "AM"));
            activities.add(item);
        }
        
        return activities;
    }
    
    public List<Map<String, Object>> getDetailedReport(Map<String, String> filters) {
        List<Map<String, Object>> report = new ArrayList<>();
        LocalDate end = LocalDate.now();
        String[] services = {"Basic Wash", "Premium Wash", "Deluxe Wash"};
        
        for (int i = 0; i < 10; i++) {
            LocalDate date = end.minusDays(i);
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("bookings", 3 + new Random().nextInt(15));
            item.put("revenue", 100 + new Random().nextInt(500));
            item.put("avgOrder", 20 + new Random().nextInt(25));
            item.put("topService", services[new Random().nextInt(services.length)]);
            item.put("customers", 2 + new Random().nextInt(10));
            report.add(item);
        }
        
        return report;
    }
    
    public String exportReport(String format, Map<String, String> filters) {
        List<Map<String, Object>> data = getDetailedReport(filters);
        
        StringBuilder csv = new StringBuilder();
        csv.append("Date,Bookings,Revenue,Avg Order,Top Service,Customers\n");
        
        for (Map<String, Object> item : data) {
            csv.append(String.format("%s,%d,%.2f,%.2f,%s,%d\n",
                item.get("date"),
                (int) item.get("bookings"),
                ((Number) item.get("revenue")).doubleValue(),
                ((Number) item.get("avgOrder")).doubleValue(),
                item.get("topService"),
                (int) item.get("customers")
            ));
        }
        
        return csv.toString();
    }
}