package com.kkkarwash.repository;

import com.kkkarwash.model.Booking;
import com.kkkarwash.model.User;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserOrderByBookingDateDesc(User user);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByStatus(Booking.Status status);
    List<Booking> findByBookingDateBetween(LocalDate start, LocalDate end);
    long countByStatus(Booking.Status status);
    long countByUserIdAndStatus(Long userId, Booking.Status status);
    List<Booking> findTop5ByOrderByCreatedAtDesc();
}