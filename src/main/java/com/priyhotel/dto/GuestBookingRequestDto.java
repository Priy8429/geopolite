package com.priyhotel.dto;

import com.priyhotel.constants.PaymentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class GuestBookingRequestDto {

    private String email;
    private String phone;
    private String fullName;
    private Long hotelId;
    private String couponCode;
    private Integer noOfAdults;
    private Integer noOfChildrens;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Double totalAmount;
    private Double payableAmount;
    private List<RoomBookingDto> roomBookingList;
}
