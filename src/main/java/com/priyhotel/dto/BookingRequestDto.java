package com.priyhotel.dto;

import com.priyhotel.constants.PaymentType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class BookingRequestDto {

    private Long userId;
    private Long hotelId;
    private String couponCode;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate checkInDate;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate checkOutDate;
    private PaymentType paymentType;
    private List<RoomBookingDto> roomBookingList;
}
