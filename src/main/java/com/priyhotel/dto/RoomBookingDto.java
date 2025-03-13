package com.priyhotel.dto;

import lombok.Data;

@Data
public class RoomBookingDto {

    private Long roomTypeId;
    private int noOfAdults;
    private int noOfChildrens;
}
