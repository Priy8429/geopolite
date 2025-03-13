package com.priyhotel.service;

import com.priyhotel.dto.HotelRequestDto;
import com.priyhotel.entity.Asset;
import com.priyhotel.entity.Hotel;
import com.priyhotel.entity.Location;
import com.priyhotel.entity.RoomType;
import com.priyhotel.mapper.HotelMapper;
import com.priyhotel.repository.HotelRepository;
import com.priyhotel.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelService {

    @Autowired
    HotelRepository hotelRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    AssetService assetService;

    @Autowired
    RoomTypeService roomTypeService;

    @Autowired
    HotelMapper hotelMapper;

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found!"));
    }

    public List<Hotel> getHotelsByLocation(Long locationId) {
        Optional<Location> location = locationRepository.findById(locationId);
        if(location.isPresent()){
            return hotelRepository.findByLocation(location.get());
        }else{
            throw new RuntimeException("Invalid location");
        }

    }

    public Hotel addHotel(HotelRequestDto hotelRequestDto) {
        Location location = locationRepository.findById(hotelRequestDto.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found!"));

        List<Asset> assets = assetService.getAllAssetsByIds(hotelRequestDto.getAssetIds());
        List<RoomType> roomTypes = roomTypeService.getRoomTypesByIds(hotelRequestDto.getRoomTypeIds());

        Hotel newHotel = hotelMapper.toEntity(hotelRequestDto);
        newHotel.setLocation(location);
        newHotel.setAssets(assets);
        newHotel.setRoomTypes(roomTypes);
        return hotelRepository.save(newHotel);
    }

    public Hotel updateHotel(Long id, HotelRequestDto hotelRequestDto) {
        Location location = locationRepository.findById(hotelRequestDto.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found!"));

        List<RoomType> roomTypes = roomTypeService.getRoomTypesByIds(hotelRequestDto.getRoomTypeIds());
        List<Asset> assets = assetService.getAllAssetsByIds(hotelRequestDto.getAssetIds());
        return hotelRepository.findById(id)
                .map(hotel -> {
                    hotel.setName(hotelRequestDto.getHotelName());
                    hotel.setLocation(location);
                    hotel.setAddress(hotelRequestDto.getHotelFullAddress());
                    hotel.setContactNumber(hotelRequestDto.getHotelPhoneNumber());
                    hotel.setEmail(hotelRequestDto.getHotelEmail());
                    hotel.setRoomTypes(roomTypes);
                    hotel.setAssets(assets);
                    return hotelRepository.save(hotel);
                })
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
    }

    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }

    public void addRoomTypesToHotel(Long hotelId, List<Long> roomTypeIds) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        List<RoomType> roomTypes = roomTypeService.getRoomTypesByIds(roomTypeIds);
        hotel.getRoomTypes().addAll(roomTypes);

        hotelRepository.save(hotel);
    }

    public List<RoomType> getRoomTypesForHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        return hotel.getRoomTypes();
    }
}
