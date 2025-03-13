package com.priyhotel.controller;

import com.priyhotel.entity.Location;
import com.priyhotel.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class LocationController {

    @Autowired
    LocationService locationService;

    @GetMapping("/locations")
    public ResponseEntity<List<Location>> getAllLocations(){
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id){
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @PostMapping("/locations")
    public ResponseEntity<Location> addNewLocation(@RequestBody Location location){
        return ResponseEntity.ok(locationService.addNewLocation(location));
    }
}
