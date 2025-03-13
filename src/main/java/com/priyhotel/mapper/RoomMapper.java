package com.priyhotel.mapper;

import com.priyhotel.dto.RoomRequestDto;
import com.priyhotel.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public Room toEntity(RoomRequestDto roomRequestDto){
        Room room = new Room();
        room.setRoomNumber(roomRequestDto.getRoomNumber());
        room.setAvailable(roomRequestDto.isRoomAvailable());
        return room;
    }
}
