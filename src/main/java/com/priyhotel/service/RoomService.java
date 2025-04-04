package com.priyhotel.service;

import com.priyhotel.dto.RoomRequestDto;
import com.priyhotel.entity.*;
import com.priyhotel.exception.BadRequestException;
import com.priyhotel.exception.ResourceNotFoundException;
import com.priyhotel.mapper.RoomMapper;
import com.priyhotel.repository.HotelRepository;
import com.priyhotel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private AmenityService amenityService;

    @Autowired
    private AssetService assetService;

    @Autowired RoomTypeService roomTypeService;

    @Autowired
    private RoomMapper roomMapper;

    public Room addRoom(Long hotelId, RoomRequestDto roomRequestDto) {

        Hotel hotel = hotelService.getHotelById(hotelId);
        RoomType roomType = roomTypeService.getRoomTypeById(roomRequestDto.getRoomTypeId());

        Room newRoom = roomMapper.toEntity(roomRequestDto);

        newRoom.setHotel(hotel);
        newRoom.setRoomType(roomType);
        return roomRepository.save(newRoom);
    }

    public List<Room> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    public Room updateRoom(Long id, RoomRequestDto updatedRoom) {
        RoomType roomType = roomTypeService.getRoomTypeById(updatedRoom.getRoomTypeId());
        Room existingRoom = getRoomById(id);
        existingRoom.setRoomNumber(updatedRoom.getRoomNumber());
        existingRoom.setRoomType(roomType);
        existingRoom.setAvailable(updatedRoom.isRoomAvailable());
        return roomRepository.save(existingRoom);
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    public List<Room> getAvailableRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelIdAndAvailable(hotelId, true);
    }

    public List<Room> findAvailableRoomsForRoomTypes(Long hotelId, Map<Long, Integer> roomTypeToQuantityMap, List<String> alreadyBookedRooms) {
        List<Room> availableRooms = new ArrayList<>();
        roomTypeToQuantityMap.forEach((roomTypeId, quantity) -> {
            //fetch not booked rooms
            List<Room> foundRooms = roomRepository.findAvailableRooms(hotelId, roomTypeId, alreadyBookedRooms, PageRequest.of(0, quantity));

            if(foundRooms.size() < quantity){
                throw new BadRequestException("Room/s not available for the selected dates!");
            }

            availableRooms.addAll(foundRooms);
        });
        return availableRooms;
    }
}
