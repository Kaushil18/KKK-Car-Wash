package com.kkkarwash.controller;

import com.kkkarwash.dto.BookingRequest;
import com.kkkarwash.model.Booking;
import com.kkkarwash.service.BookingService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.List;

@Controller("/bookings")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class BookingController {
    
    private final BookingService bookingService;
    
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    @Get
    public HttpResponse<List<Booking>> getMyBookings() {
        return HttpResponse.ok(bookingService.getUserBookings());
    }
    
    @Post
    public HttpResponse<Booking> createBooking(@Body BookingRequest request) {
        return HttpResponse.created(bookingService.createBooking(request));
    }
    
    @Put("/{id}/cancel")
    public HttpResponse<?> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return HttpResponse.ok();
    }
    
    @Put("/{id}/reschedule")
    public HttpResponse<Booking> rescheduleBooking(@PathVariable Long id, @Body BookingRequest request) {
        return HttpResponse.ok(bookingService.rescheduleBooking(id, request));
    }
    
    @Get("/check-availability")
    public HttpResponse<Boolean> checkAvailability(@QueryValue String date, @QueryValue String time) {
        return HttpResponse.ok(bookingService.checkAvailability(date, time));
    }
}