package com.priyhotel.controller;

import com.priyhotel.constants.Constants;
import com.priyhotel.dto.OffersUpdateDto;
import com.priyhotel.dto.RoomTypeRequestDto;
import com.priyhotel.entity.Asset;
import com.priyhotel.entity.RoomType;
import com.priyhotel.service.AssetService;
import com.priyhotel.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/room-types")
@CrossOrigin(origins = "*")
public class RoomTypeController {

    @Autowired
    RoomTypeService roomTypeService;

    @Autowired
    AssetService assetService;

    @GetMapping
    public ResponseEntity<List<RoomType>> getAllRoomTypes(){
        return ResponseEntity.ok(roomTypeService.getAllRoomTypes());
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<?> getRoomTypesByHotel(@PathVariable Long hotelId){
        return ResponseEntity.ok(roomTypeService.getRoomTypesByHotelId(hotelId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomType> getRoomTypeById(@PathVariable Long id){
        return ResponseEntity.ok(roomTypeService.getRoomTypeById(id));
    }

    @PostMapping
    public ResponseEntity<RoomType> addRoomType(@RequestBody RoomTypeRequestDto roomTypeRequestDto){
        return ResponseEntity.ok(roomTypeService.addRoomType(roomTypeRequestDto));
    }

    @PatchMapping("/{roomTypeId}")
    public ResponseEntity<RoomType> updateRoomType(@PathVariable Long roomTypeId, @RequestBody RoomTypeRequestDto roomTypeRequestDto){
        return ResponseEntity.ok(roomTypeService.updateRoomType(roomTypeId, roomTypeRequestDto));
    }

    @DeleteMapping("/{roomTypeId}")
    public ResponseEntity<Boolean> deleteRoomById(@PathVariable Long roomTypeId){
        return ResponseEntity.ok(roomTypeService.deleteRoomById(roomTypeId));
    }

    @PostMapping("/assets/upload")
    public ResponseEntity<List<Asset>> uploadAssets(@RequestParam("files") List<MultipartFile> files){
        return ResponseEntity.ok(assetService.uploadAssets(files, Constants.ROOM_TYPE_DIR_NAME));
    }

    @DeleteMapping("/assets/remove")
    public ResponseEntity<?> removeAsset(@RequestParam Long assetId){
        return ResponseEntity.ok(assetService.removeAsset(assetId));
    }

    @PatchMapping("/update-offers")
    public ResponseEntity<?> updateOffers(@RequestBody OffersUpdateDto offersUpdateDto){
        return ResponseEntity.ok(roomTypeService.updateOffers(offersUpdateDto));
    }
}
