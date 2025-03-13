package com.priyhotel.service;

import com.priyhotel.dto.RoomTypeRequestDto;
import com.priyhotel.entity.Amenity;
import com.priyhotel.entity.Asset;
import com.priyhotel.entity.RoomType;
import com.priyhotel.mapper.RoomTypeMapper;
import com.priyhotel.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomTypeService {

    @Autowired
    RoomTypeRepository roomTypeRepository;

    @Autowired
    AssetService assetService;

    @Autowired
    AmenityService amenityService;

    @Autowired
    RoomTypeMapper roomTypeMapper;

    public RoomType addRoomType(RoomTypeRequestDto roomTypeRequestDto){
        List<Amenity> amenities = amenityService.getAmenitiesByIds(roomTypeRequestDto.getAmenityIds());
        List<Asset> assets = assetService.getAllAssetsByIds(roomTypeRequestDto.getAssetIds());

        RoomType roomType = roomTypeMapper.toEntity(roomTypeRequestDto);
        roomType.setAmenities(amenities);
        roomType.setAssets(assets);
        return roomTypeRepository.save(roomType);
    }

    public RoomType updateRoomType(Long roomTypeId, RoomTypeRequestDto roomTypeRequestDto){
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("Room type not found!"));

        List<Amenity> amenities = amenityService.getAmenitiesByIds(roomTypeRequestDto.getAmenityIds());
        List<Asset> assets = assetService.getAllAssetsByIds(roomTypeRequestDto.getAssetIds());

        roomType.setTypeName(roomTypeRequestDto.getTypeName());
        roomType.setCapacityAdult(roomTypeRequestDto.getCapacityAdult());
        roomType.setCapacityChild(roomTypeRequestDto.getCapacityChild());
        roomType.setDescription(roomTypeRequestDto.getDescription());
        roomType.setPricePerNight(roomTypeRequestDto.getPricePerNight());
        roomType.setRoomSizeInSquareFeet(roomType.getRoomSizeInSquareFeet());
        roomType.setAmenities(amenities);
        roomType.setAssets(assets);
        return roomTypeRepository.save(roomType);
    }

    public boolean deleteRoomById(Long id){
        roomTypeRepository.deleteById(id);
        return true;
    }

    public List<RoomType> getRoomTypesByIds(List<Long> roomTypeIds) {
        return roomTypeRepository.findAllById(roomTypeIds);
    }

    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }

    public RoomType getRoomTypeById(Long id) {
        return roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room type not found!"));
    }
}
