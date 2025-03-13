package com.priyhotel.repository;

import com.priyhotel.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByRoomIdAndCheckInDateBetweenOrCheckOutDateBetween(
            Long roomId, LocalDate checkInStart, LocalDate checkInEnd, LocalDate checkOutStart, LocalDate checkOutEnd
    );

    List<Booking> findByHotelIdAndCheckInDateBetweenOrCheckOutDateBetween(
            Long roomId, LocalDate checkInStart, LocalDate checkInEnd, LocalDate checkOutStart, LocalDate checkOutEnd
    );
}
