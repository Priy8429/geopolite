package com.priyhotel.dto;

import com.priyhotel.constants.BookingStatus;
import com.priyhotel.constants.PaymentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class BookingResponseDto {

    private String bookingNumber;

    private UserDto user;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Double totalAmount;

//    private Boolean couponApplied;
//
//    private String couponCode;

    private Double payableAmount;

    private Integer totalRooms;

    private Integer noOfAdults;

    private Integer noOfChildrens;

    private List<RoomDto> rooms;

    private PaymentType paymentType; // PREPAID, POSTPAID, PARTIALLY_PAID

    private BookingStatus status; // CONFIRMED, CANCELLED, COMPLETED
}
