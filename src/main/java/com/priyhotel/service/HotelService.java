package com.priyhotel.service;

import com.priyhotel.dto.HotelRequestDto;
import com.priyhotel.entity.Asset;
import com.priyhotel.entity.Hotel;
import com.priyhotel.entity.Location;
import com.priyhotel.entity.RoomType;
import com.priyhotel.exception.ResourceNotFoundException;
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
    LocationService locationService;

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
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found!"));
    }

    public List<Hotel> getHotelsByLocation(Long locationId) {
        Location location = locationService.getLocationById(locationId);
        return hotelRepository.findByLocation(location);
    }

    public Hotel getReferenceById(Long id){
        return hotelRepository.getReferenceById(id);
    }

    public Hotel addHotel(HotelRequestDto hotelRequestDto) {

        Location location = locationService.getLocationById(hotelRequestDto.getLocationId());

        List<Asset> assets = assetService.getAllAssetsByIds(hotelRequestDto.getAssetIds());
        List<RoomType> roomTypes = roomTypeService.getRoomTypesByIds(hotelRequestDto.getRoomTypeIds());

        Hotel newHotel = hotelMapper.toEntity(hotelRequestDto);
        newHotel.setLocation(location);
        newHotel.setAssets(assets);
        newHotel.setRoomTypes(roomTypes);
        return hotelRepository.save(newHotel);
    }

    public Hotel updateHotel(Long id, HotelRequestDto dto){
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        if (dto.getHotelName()!= null) hotel.setName(dto.getHotelName());
        if (dto.getHotelFullAddress() != null) hotel.setAddress(dto.getHotelFullAddress());
        if (dto.getHotelPhoneNumber() != null) hotel.setContactNumber(dto.getHotelPhoneNumber());
        if (dto.getHotelEmail() != null) hotel.setEmail(dto.getHotelEmail());

        if (dto.getLocationId() != null) {
            Location location = locationService.getLocationById(dto.getLocationId());
            hotel.setLocation(location);
        }

        if (dto.getRoomTypeIds() != null) {
            List<RoomType> roomTypes = roomTypeService.getRoomTypesByIds((dto.getRoomTypeIds()));
            hotel.setRoomTypes(roomTypes);
        }

        if (dto.getAssetIds() != null) {
            List<Asset> assets = assetService.getAllAssetsByIds(dto.getAssetIds());
            hotel.setAssets(assets);
        }

        return hotelRepository.save(hotel);
    }

    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }

    public void addRoomTypesToHotel(Long hotelId, List<Long> roomTypeIds) {
        Hotel hotel = this.getHotelById(hotelId)
;
        List<RoomType> roomTypes = roomTypeService.getRoomTypesByIds(roomTypeIds);
        hotel.getRoomTypes().addAll(roomTypes);

        hotelRepository.save(hotel);
    }

    public List<RoomType> getRoomTypesForHotel(Long hotelId) {
        Hotel hotel = this.getHotelById(hotelId);
        return hotel.getRoomTypes();
    }

}
