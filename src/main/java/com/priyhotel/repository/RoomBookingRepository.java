package com.priyhotel.repository;

import com.priyhotel.entity.RoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {
    void deleteByBookingId(Long id);

    List<RoomBooking> findAllByBookingId(Long id);

    @Modifying
    @Query("DELETE FROM RoomBooking rb WHERE rb.booking.id = ?1")
    void deleteAllByBookingId(Long id);
}
