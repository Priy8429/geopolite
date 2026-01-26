package com.priyhotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCheckoutRequestDto {

    private String bookingNumber;
    private LocalDate newCheckoutDate;
    private Double newBookingAmount;
}
