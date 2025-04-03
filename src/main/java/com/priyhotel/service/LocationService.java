package com.priyhotel.service;

import com.priyhotel.entity.Location;
import com.priyhotel.exception.ResourceNotFoundException;
import com.priyhotel.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    LocationRepository locationRepository;

    public List<Location> getAllLocations(){
        return locationRepository.findAll();
    }

    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found!"));
    }

    public Location addNewLocation(Location location) {
        return locationRepository.save(location);
    }
}
