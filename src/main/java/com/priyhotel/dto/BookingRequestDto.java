package com.priyhotel.dto;

import com.priyhotel.constants.BookingSource;
import com.priyhotel.constants.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {

    private Long userId;
    private Long hotelId;
    private String couponCode;
    private Integer noOfAdults;
    private Integer noOfChildrens;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private PaymentType paymentType;
    private Double totalAmount;
    private Double payableAmount;
    private BookingSource bookingSource;
    private String specialRequest;
    private List<RoomBookingDto> roomBookingList;
}
