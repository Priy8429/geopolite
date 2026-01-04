package com.priyhotel.mapper;

import com.priyhotel.dto.RoomTypeAvailabilityDto;
import com.priyhotel.dto.RoomTypeDto;
import com.priyhotel.dto.RoomTypeRequestDto;
import com.priyhotel.entity.Amenity;
import com.priyhotel.entity.RoomType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomTypeMapper {

    public RoomType toEntity(RoomTypeRequestDto roomTypeRequestDto){
        RoomType roomType = new RoomType();
        roomType.setTypeName(roomTypeRequestDto.getTypeName());
        roomType.setCapacityAdult(roomTypeRequestDto.getCapacityAdult());
        roomType.setCapacityChild(roomTypeRequestDto.getCapacityChild());
        roomType.setPricePerNight(roomTypeRequestDto.getPricePerNight());
        roomType.setDescription(roomTypeRequestDto.getDescription());
        roomType.setRoomSizeInSquareFeet(roomTypeRequestDto.getRoomSizeInSquareFeet());
        return roomType;
    }

    public RoomTypeDto toDto(RoomType roomType){
        return RoomTypeDto.builder()
                .id(roomType.getId())
                .typeName(roomType.getTypeName())
                .capacityAdult(roomType.getCapacityAdult())
                .capacityChild(roomType.getCapacityChild())
                .pricePerNight(roomType.getPricePerNight())
                .offerStartDate(roomType.getOfferStartDate())
                .offerEndDate(roomType.getOfferEndDate())
                .offerDiscountPercentage(roomType.getOfferDiscountPercentage())
                .description(roomType.getDescription())
                .roomSizeInSquareFeet(roomType.getRoomSizeInSquareFeet())
                .amenities(roomType.getAmenities().stream().map(Amenity::getAmenityDescription).toList())
                .assets(roomType.getAssets())
                .build();
    }

    public List<RoomTypeDto> toDtos(List<RoomType> roomTypes){
        return roomTypes.stream().map(this::toDto).toList();
    }

    public RoomTypeAvailabilityDto toRTAvailabilityResponse(RoomType roomType){
        return RoomTypeAvailabilityDto.builder()
                .id(roomType.getId())
                .typeName(roomType.getTypeName())
                .capacityAdult(roomType.getCapacityAdult())
                .capacityChild(roomType.getCapacityChild())
                .pricePerNight(roomType.getPricePerNight())
                .offerDiscountPercentage(roomType.getOfferDiscountPercentage())
                .offerStartDate(roomType.getOfferStartDate())
                .offerEndDate(roomType.getOfferEndDate())
                .description(roomType.getDescription())
                .roomSizeInSquareFeet(roomType.getRoomSizeInSquareFeet())
                .amenities(roomType.getAmenities())
                .assets(roomType.getAssets())
                .build();
    }

    public List<RoomTypeAvailabilityDto> toRTAvailabilityDtos(List<RoomType> roomTypes){
        return roomTypes.stream().map(this::toRTAvailabilityResponse).toList();
    }
}
