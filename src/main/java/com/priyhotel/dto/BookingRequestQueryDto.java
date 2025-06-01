package com.priyhotel.dto;

import lombok.Data;

@Data
public class BookingRequestQueryDto {
    private Long hotelId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Integer noOfGuests;
    private Integer noOfRooms;
}
