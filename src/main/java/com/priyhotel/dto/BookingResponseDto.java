package com.priyhotel.dto;

import com.priyhotel.constants.BookingSource;
import com.priyhotel.constants.BookingStatus;
import com.priyhotel.constants.PaymentStatus;
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

    private BookingSource bookingSource;

    private String specialRequest;

    private PaymentType paymentType; // PREPAID, POSTPAID, PARTIALLY_PAID

    private BookingStatus status; // CONFIRMED, CANCELLED, COMPLETED

    private PaymentStatus paymentStatus;

    private LocalDate paymentDate;

    private Double paymentAmount;
}
