package com.priyhotel.dto;

import com.priyhotel.constants.BookingSource;
import com.priyhotel.constants.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventBookingRequestDto {

    private String userFullName;
    private String userEmail;
    private String userPhoneNumber;
    private Long hotelId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
