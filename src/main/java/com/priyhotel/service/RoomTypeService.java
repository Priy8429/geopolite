package com.priyhotel.service;

import com.priyhotel.dto.OffersUpdateDto;
import com.priyhotel.dto.RoomTypeDto;
import com.priyhotel.dto.RoomTypeRequestDto;
import com.priyhotel.entity.Amenity;
import com.priyhotel.entity.Asset;
import com.priyhotel.entity.Hotel;
import com.priyhotel.entity.RoomType;
import com.priyhotel.exception.ResourceNotFoundException;
import com.priyhotel.mapper.RoomTypeMapper;
import com.priyhotel.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public RoomType updateRoomType(Long roomTypeId, RoomTypeRequestDto dto) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found!"));

        if (dto.getTypeName() != null) {
            roomType.setTypeName(dto.getTypeName());
        }

        if (dto.getCapacityAdult() != null) {
            roomType.setCapacityAdult(dto.getCapacityAdult());
        }

        if (dto.getCapacityChild() != null) {
            roomType.setCapacityChild(dto.getCapacityChild());
        }

        if (dto.getDescription() != null) {
            roomType.setDescription(dto.getDescription());
        }

        if (dto.getPricePerNight() != null) {
            roomType.setPricePerNight(dto.getPricePerNight());
        }

        if (dto.getRoomSizeInSquareFeet() != null) {
            roomType.setRoomSizeInSquareFeet(dto.getRoomSizeInSquareFeet());
        }

        if (dto.getAmenityIds() != null && !dto.getAmenityIds().isEmpty()) {
            List<Amenity> amenities = amenityService.getAmenitiesByIds(dto.getAmenityIds());
            roomType.setAmenities(amenities);
        }

        if (dto.getAssetIds() != null && !dto.getAssetIds().isEmpty()) {
            List<Asset> assets = assetService.getAllAssetsByIds(dto.getAssetIds());
            roomType.setAssets(assets);
        }

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
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found!"));
    }

    public List<RoomTypeDto> getRoomTypesByHotelId(Long hotelId) {
        List<RoomType> roomTypes = roomTypeRepository.findRoomTypesByHotel(hotelId);
        List<RoomTypeDto> roomTypeDtos = roomTypeMapper.toDtos(roomTypes);

        roomTypeDtos.forEach(roomTypeDto -> {
            roomTypeDto.setOfferPrice(this.getAmountAfterDiscount(roomTypeDto.getOfferStartDate(), roomTypeDto.getOfferEndDate(), roomTypeDto.getPricePerNight(), roomTypeDto.getOfferDiscountPercentage()));
        });
        return roomTypeDtos;
    }

    public String updateOffers(OffersUpdateDto offersUpdateDto) {
        List<RoomType> roomTypes = roomTypeRepository.findRoomTypesByHotel(offersUpdateDto.getHotelId());
        roomTypes.forEach(roomType -> {
            Double discountPercent = offersUpdateDto.getOfferRoomMap().get(roomType.getTypeName());
            roomType.setOfferDiscountPercentage(discountPercent);
            roomType.setOfferStartDate(offersUpdateDto.getOfferStartDate());
            roomType.setOfferEndDate(offersUpdateDto.getOfferEndDate());
        });
        roomTypeRepository.saveAll(roomTypes);
        return "Offers updated!";
    }

    public Double getAmountAfterDiscount(LocalDate offerStartDate, LocalDate offerEndDate, Double pricePerNight, Double discountPercentage){
        if(Objects.nonNull(offerEndDate) &&
                !offerStartDate.isAfter(LocalDate.now()) &&
                !offerEndDate.isBefore(LocalDate.now())){
            double discount = pricePerNight * (discountPercentage / 100.0);
            return pricePerNight - discount;
        }else{
            return pricePerNight;
        }
    }
}
