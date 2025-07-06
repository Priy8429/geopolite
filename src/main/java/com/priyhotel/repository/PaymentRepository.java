package com.priyhotel.repository;

import com.priyhotel.entity.Booking;
import com.priyhotel.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRazorpayOrderId(String orderId);

    Optional<Payment> findByBooking(Booking booking);
}
