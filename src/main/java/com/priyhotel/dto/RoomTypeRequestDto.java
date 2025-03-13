package com.priyhotel.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoomTypeRequestDto {

    private String typeName;

    private int capacityAdult;

    private int capacityChild;

    private double pricePerNight;

    private String description;

    private int roomSizeInSquareFeet;

    private List<Long> amenityIds;

    private List<Long> assetIds;
}
