package com.priyhotel.repository;

import com.priyhotel.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    
    Refund findByRefundId(String refundId);

    Refund findByPaymentId(Long id);
}
