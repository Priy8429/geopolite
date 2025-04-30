package com.priyhotel.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomBookingDto {

    private Long roomTypeId;
    private Integer noOfRooms;
}
