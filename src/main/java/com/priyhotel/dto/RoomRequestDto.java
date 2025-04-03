package com.priyhotel.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoomRequestDto {
    private String roomNumber;
    private boolean roomAvailable;
    private Long hotelId;
    private Long roomTypeId;
}
