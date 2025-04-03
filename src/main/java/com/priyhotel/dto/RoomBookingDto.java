package com.priyhotel.dto;

import lombok.Data;

@Data
public class RoomBookingDto {

    private Long roomTypeId;
    private Integer noOfAdults;
    private Integer noOfChildrens;
}
