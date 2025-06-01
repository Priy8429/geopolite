package com.priyhotel.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoomTypeRequestDto {

    private String typeName;

    private Integer capacityAdult;

    private Integer capacityChild;

    private Double pricePerNight;

    private String description;

    private Integer roomSizeInSquareFeet;

    private List<Long> amenityIds;

    private List<Long> assetIds;
}
