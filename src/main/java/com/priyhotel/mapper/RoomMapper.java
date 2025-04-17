package com.priyhotel.mapper;

import com.priyhotel.dto.RoomDto;
import com.priyhotel.dto.RoomRequestDto;
import com.priyhotel.entity.Room;
import com.priyhotel.entity.RoomBooking;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomMapper {

    public Room toEntity(RoomRequestDto roomRequestDto){
        Room room = new Room();
        room.setRoomNumber(roomRequestDto.getRoomNumber());
        room.setAvailable(roomRequestDto.isRoomAvailable());
        return room;
    }

    public RoomDto toDto(Room room){
        return RoomDto.builder()
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType().getTypeName())
                .build();
    }

    public List<RoomDto> toDtos(List<Room> rooms){
        return rooms.stream().map(this::toDto).toList();
    }

    public RoomDto toDtoFromRoomBooking(RoomBooking roomBooking){
        return RoomDto.builder()
                .roomNumber(roomBooking.getRoom().getRoomNumber())
                .roomType(roomBooking.getRoom().getRoomType().getTypeName())
                .build();
    }

    public List<RoomDto> toDtosFromRoomBooking(List<RoomBooking> roomBookings){
        return roomBookings.stream().map(this::toDtoFromRoomBooking).toList();
    }
}
