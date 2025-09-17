package com.priyhotel.dto;

import com.priyhotel.constants.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {

    private PaymentStatus status;
    private Double amount;
    private LocalDateTime paymentDate;
}
