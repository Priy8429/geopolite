package com.priyhotel.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.priyhotel.constants.BookingStatus;
import com.priyhotel.constants.PaymentType;
import com.priyhotel.entity.RoomBooking;
import com.priyhotel.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class BookingDto {

    private String bookingNumber;
    private UserDto user;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Double totalAmount;
    private Boolean couponApplied;
    private String couponCode;
    private Double payableAmount;
    private Integer totalRooms;
    private Integer noOfAdults;
    private Integer noOfChildrens;
    private List<RoomBooking> bookedRooms;
    private PaymentType paymentType; // PREPAID, POSTPAID, PARTIALLY_PAID
    private BookingStatus status; // CONFIRMED, CANCELLED, COMPLETED
}
