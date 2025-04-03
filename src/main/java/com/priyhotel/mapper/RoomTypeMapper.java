package com.priyhotel.mapper;

import com.priyhotel.dto.RoomTypeRequestDto;
import com.priyhotel.entity.RoomType;
import org.springframework.stereotype.Component;

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
}
