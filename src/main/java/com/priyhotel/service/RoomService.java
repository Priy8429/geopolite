package com.priyhotel.service;

import com.priyhotel.dto.BookingRequestDto;
import com.priyhotel.dto.RoomBookingDto;
import com.priyhotel.dto.RoomRequestDto;
import com.priyhotel.entity.*;
import com.priyhotel.exception.BadRequestException;
import com.priyhotel.exception.ResourceNotFoundException;
import com.priyhotel.mapper.RoomMapper;
import com.priyhotel.repository.BookingRepository;
import com.priyhotel.repository.HotelRepository;
import com.priyhotel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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
    BookingRepository bookingRepository;

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

        Room existingRoom = getRoomById(id);

        if(updatedRoom.getRoomNumber() != null) {
            existingRoom.setRoomNumber(updatedRoom.getRoomNumber());
        }

        if(updatedRoom.getRoomTypeId() != null){
            RoomType roomType = roomTypeService.getRoomTypeById(updatedRoom.getRoomTypeId());
            existingRoom.setRoomType(roomType);
        }

        existingRoom.setAvailable(updatedRoom.isRoomAvailable());
        roomRepository.save(existingRoom);
        return existingRoom;
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    public List<Room> getAvailableRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelIdAndAvailable(hotelId, true);
    }

    public List<Room> getAllRoomsByHotelWithAvailableStatus(Long hotelId){
        LocalDate today = LocalDate.now();
        List<Room> allRooms = roomRepository.findByHotelId(hotelId);
        List<String> alreadyBookedRoomNumbers = this.getAlreadyBookedRoomNumbers(hotelId, today, today.plusDays(1));
        return allRooms.stream().map(room -> {
            room.setAvailable(!alreadyBookedRoomNumbers.contains(room.getRoomNumber()));
            return room;
        }).toList();
    }

    public List<String> getAlreadyBookedRoomNumbers(Long hotelId, LocalDate checkinDate, LocalDate checkoutDate){
        return bookingRepository.findBookedRoomNumbers(hotelId, checkinDate, checkoutDate);
    }

    public List<Room> findAvailableRoomsForRoomTypes(Long hotelId, List<RoomBookingDto> roomBookingDtos, List<String> alreadyBookedRooms) {
        List<Room> availableRooms = new ArrayList<>();
        roomBookingDtos.forEach(request -> {
            //fetch not booked rooms
            List<Room> foundRooms = roomRepository.findAvailableRooms(hotelId, request.getRoomTypeId(), alreadyBookedRooms, PageRequest.of(0, request.getNoOfRooms()));

            if(foundRooms.size() < request.getNoOfRooms()){
                throw new BadRequestException("Room/s not available for the selected dates!");
            }

            availableRooms.addAll(foundRooms);
        });
        return availableRooms;
    }

    public Map<String, Long> findAvailableRoomsCountForRoomTypes(Long hotelId, List<String> alreadyBookedRoomNumbers) {
        List<Object[]> results = roomRepository.countAvailableRoomsGroupedByType(hotelId, alreadyBookedRoomNumbers);
        Map<String, Long> roomTypeCounts = new HashMap<>();
        for(Object[] result: results){
            RoomType roomType = (RoomType) result[0];
            Long count = (Long) result[1];
            roomTypeCounts.put(roomType.getTypeName(), count);
        }
        return roomTypeCounts;
    }

}
