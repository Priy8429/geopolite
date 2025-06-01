package com.priyhotel.entity;

import com.priyhotel.constants.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String razorpayOrderId;

    @Column(nullable = true)
    private String razorpayPaymentId;

    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = true)
    private Booking booking;
}
