package com.kkkarwash.repository;

import com.kkkarwash.model.Payment;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByBookingId(Long bookingId);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    List<Payment> findByMethod(Payment.PaymentMethod method);
    
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
    
    List<Payment> findByPaymentDateAfter(LocalDateTime date);
    
    long countByStatus(Payment.PaymentStatus status);
    
    long countByMethod(Payment.PaymentMethod method);
    
    Optional<Payment> findByTransactionId(String transactionId);
}