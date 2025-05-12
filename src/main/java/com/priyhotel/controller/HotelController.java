package com.priyhotel.controller;

import com.priyhotel.constants.Constants;
import com.priyhotel.dto.HotelRequestDto;
import com.priyhotel.entity.Asset;
import com.priyhotel.entity.Hotel;
import com.priyhotel.service.AssetService;
import com.priyhotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@CrossOrigin(origins = "*")
public class HotelController {

    @Autowired
    HotelService hotelService;

    @Autowired
    AssetService assetService;

    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping("/location/{locationId}")
    public List<Hotel> getHotelsByLocation(@PathVariable Long locationId) {
        return hotelService.getHotelsByLocation(locationId);
    }

    @PostMapping
    public ResponseEntity<Hotel> addHotel(@RequestBody HotelRequestDto hotelDto) {
        return ResponseEntity.ok(hotelService.addHotel(hotelDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateHotel(@PathVariable Long id, @RequestBody HotelRequestDto hotelRequestDto){
        return ResponseEntity.ok(hotelService.updateHotel(id, hotelRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.ok("Hotel deleted successfully!");
    }

    @PostMapping("/assets/upload")
    public ResponseEntity<List<Asset>> uploadAssets(@RequestParam("files") List<MultipartFile> files){
        return ResponseEntity.ok(assetService.uploadAssets(files, Constants.HOTEL_DIR_NAME));
    }

}
