package com.priyhotel.mapper;

import com.priyhotel.dto.HotelRequestDto;
import com.priyhotel.entity.Hotel;
import org.springframework.stereotype.Component;

@Component
public class HotelMapper {

    public Hotel toEntity(HotelRequestDto hotelRequestDto){
        Hotel hotel = new Hotel();
        hotel.setName(hotelRequestDto.getHotelName());
        hotel.setAddress(hotelRequestDto.getHotelFullAddress());
        hotel.setEmail(hotelRequestDto.getHotelEmail());
        hotel.setContactNumber(hotelRequestDto.getHotelPhoneNumber());
        return hotel;
    }
}
