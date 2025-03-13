package com.priyhotel.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private double discountAmount; // Fixed discount
    private double discountPercentage; // Percentage discount
    private double minOrderAmount; // Minimum order value required
    private double maxDiscountAmount;
    private boolean active;
    private LocalDateTime expiryDate;
}
