package com.priyhotel.dto;

import lombok.Data;

import java.util.List;

@Data
public class HotelRequestDto {
    private String hotelName;
    private String hotelFullAddress;
    private String hotelPhoneNumber;
    private String hotelEmail;
    private Long locationId;
    private List<Long> roomTypeIds;
    private List<Long> assetIds;
}
