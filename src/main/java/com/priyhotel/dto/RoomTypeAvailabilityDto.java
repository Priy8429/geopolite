package com.priyhotel.dto;

import com.priyhotel.entity.Amenity;
import com.priyhotel.entity.Asset;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class RoomTypeAvailabilityDto {

    private Long id;

    private String typeName;

    private Integer capacityAdult;

    private Integer capacityChild;

    private Double pricePerNight;

    private Double offerDiscountPercentage;

    private LocalDate offerStartDate;

    private LocalDate offerEndDate;

    private String description;

    private Integer roomSizeInSquareFeet;

    private List<Amenity> amenities;

    private List<Asset> assets;

    private Long availableRoomsQty;
}
