package com.priyhotel.mapper;

import com.priyhotel.dto.RoomBookingDto;
import com.priyhotel.entity.Booking;
import com.priyhotel.entity.RoomBookingRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomBookingMapper {

    public RoomBookingRequest toEntity(RoomBookingDto roomBookingDto, Booking booking){
        RoomBookingRequest roomBookingRequest = new RoomBookingRequest();
        roomBookingRequest.setBooking(booking);
        roomBookingRequest.setRoomTypeId(roomBookingDto.getRoomTypeId());
        roomBookingRequest.setNoOfRooms(roomBookingDto.getNoOfRooms());
        return roomBookingRequest;
    }

    public List<RoomBookingRequest> toEntities(List<RoomBookingDto> roomBookingDtos, Booking booking){
        return roomBookingDtos.stream().map(dto -> this.toEntity(dto, booking)).toList();
    }

    public RoomBookingDto toDto(RoomBookingRequest roomBookingRequest){
        return RoomBookingDto.builder()
                .roomTypeId(roomBookingRequest.getRoomTypeId())
                .noOfRooms(roomBookingRequest.getNoOfRooms())
                .build();
    }

    public List<RoomBookingDto> toDtos(List<RoomBookingRequest> roomBookingRequests){
        return roomBookingRequests.stream().map(this::toDto).toList();
    }
}
