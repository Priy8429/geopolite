package com.priyhotel.repository;

import com.priyhotel.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    @Query("SELECT DISTINCT rt FROM Hotel h " +
            "JOIN h.roomTypes rt " +
            "WHERE h.id = :hotelId ")
    List<RoomType> findRoomTypesByHotel(@Param("hotelId") Long hotelId);

}
