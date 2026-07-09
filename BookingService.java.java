package com.kkkarwash.service;

import com.kkkarwash.dto.BookingRequest;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Singleton
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    
    public BookingService(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }
    
    public List<Booking> getUserBookings() {
        // Get current user from security context
        // For demo, return all bookings for user 2
        return bookingRepository.findByUserId(2L);
    }
    
    @Transactional
    public Booking createBooking(BookingRequest request) {
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Service not found"));
        
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        // Check availability
        if (!checkAvailability(request.getDate(), request.getTime())) {
            throw new HttpStatusException(HttpStatus.CONFLICT, "Time slot not available");
        }
        
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setService(service);
        booking.setBookingDate(LocalDate.parse(request.getDate()));
        booking.setBookingTime(LocalTime.parse(request.getTime()));
        booking.setVehicle(request.getVehicle());
        booking.setNotes(request.getNotes());
        booking.setStatus(Booking.Status.pending);
        booking.setTotal(service.getPrice());
        booking.setPaymentMethod(Booking.PaymentMethod.valueOf(request.getPaymentMethod()));
        
        return bookingRepository.save(booking);
    }
    
    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        
        if (booking.getStatus() == Booking.Status.cancelled) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Booking already cancelled");
        }
        
        booking.setStatus(Booking.Status.cancelled);
        bookingRepository.update(booking);
    }
    
    @Transactional
    public Booking rescheduleBooking(Long id, BookingRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        
        // Check availability for new slot
        if (!checkAvailability(request.getDate(), request.getTime())) {
            throw new HttpStatusException(HttpStatus.CONFLICT, "New time slot not available");
        }
        
        booking.setBookingDate(LocalDate.parse(request.getDate()));
        booking.setBookingTime(LocalTime.parse(request.getTime()));
        
        return bookingRepository.update(booking);
    }
    
    public boolean checkAvailability(String date, String time) {
        // In production, check against existing bookings
        // For demo, allow all except some reserved slots
        LocalDate bookingDate = LocalDate.parse(date);
        LocalTime bookingTime = LocalTime.parse(time);
        
        // Hardcoded reserved times
        List<String> reservedTimes = List.of("09:00", "13:00", "15:30");
        if (reservedTimes.contains(time)) {
            // Check if any booking exists for this date and time
            // For demo, always return false for reserved times
            return false;
        }
        
        return true;
    }
}