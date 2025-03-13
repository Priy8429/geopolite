package com.priyhotel.service;

import com.priyhotel.dto.RoomRequestDto;
import com.priyhotel.entity.Amenity;
import com.priyhotel.entity.Asset;
import com.priyhotel.entity.Hotel;
import com.priyhotel.entity.Room;
import com.priyhotel.mapper.RoomMapper;
import com.priyhotel.repository.HotelRepository;
import com.priyhotel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private AmenityService amenityService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private RoomMapper roomMapper;

    public Room addRoom(Long hotelId, RoomRequestDto roomRequestDto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        List<Amenity> amenities = amenityService.getAmenitiesByIds(roomRequestDto.getAmenityIds());
        List<Asset> assets = assetService.getAllAssetsByIds(roomRequestDto.getAssetIds());

        Room newRoom = roomMapper.toEntity(roomRequestDto);
        newRoom.setHotel(hotel);
        return roomRepository.save(newRoom);
    }

    public List<Room> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public Room updateRoom(Long id, Room updatedRoom) {
        Room existingRoom = getRoomById(id);
        existingRoom.setRoomNumber(updatedRoom.getRoomNumber());
        existingRoom.setRoomType(updatedRoom.getRoomType());
        existingRoom.setAvailable(updatedRoom.isAvailable());
        return roomRepository.save(existingRoom);
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    public List<Room> getAvailableRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelIdAndAvailable(hotelId, true);
    }
}
