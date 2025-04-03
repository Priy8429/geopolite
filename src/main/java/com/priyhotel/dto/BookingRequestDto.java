package com.priyhotel.dto;

import com.priyhotel.constants.PaymentType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class BookingRequestDto {

    private Long userId;
    private Long hotelId;
    private String couponCode;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private PaymentType paymentType;
    private List<RoomBookingDto> roomBookingList;
}
