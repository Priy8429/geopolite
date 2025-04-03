package com.priyhotel.service;

import com.priyhotel.entity.Amenity;
import com.priyhotel.repository.AmenityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmenityService {

    @Autowired
    AmenityRepository amenityRepository;

    public List<Amenity> getAmenitiesByIds(List<Long> amenityIds){
        return amenityRepository.findAllById(amenityIds);
    }
}
