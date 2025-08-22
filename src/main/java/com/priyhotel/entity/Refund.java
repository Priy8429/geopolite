package com.priyhotel.entity;

import com.priyhotel.constants.RefundStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "refunds")
public class Refund extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String refundId;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false)
    private Double refundAmount;

    @Column(nullable = true)
    private String refundReason;

    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus;
}
