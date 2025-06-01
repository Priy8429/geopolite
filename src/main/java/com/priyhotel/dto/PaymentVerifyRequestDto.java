package com.priyhotel.dto;

import lombok.Data;

@Data
public class PaymentVerifyRequestDto {
    private String paymentId;
    private String orderId;
    private String signature;
}
