package com.priyhotel.repository;

import com.priyhotel.entity.Room;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByAvailable(boolean available);

    List<Room> findByHotelId(Long hotelId);

    List<Room> findByHotelIdAndAvailable(Long hotelId, boolean available);

    List<Room> findByHotelIdAndRoomTypeId(Long hotelId, Long roomTypeId, PageRequest of);

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.roomType.id = :roomTypeId AND r.roomNumber NOT IN :excludedRoomNumbers")
    List<Room> findAvailableRooms(
            @Param("hotelId") Long hotelId,
            @Param("roomTypeId") Long roomTypeId,
            @Param("excludedRoomNumbers") List<String> excludedRoomNumbers,
            Pageable pageable);
}
