package com.priyhotel.repository;

import com.priyhotel.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

//    List<Booking> findByRoomIdAndCheckInDateBetweenOrCheckOutDateBetween(
//            Long roomId, LocalDate checkInStart, LocalDate checkInEnd, LocalDate checkOutStart, LocalDate checkOutEnd
//    );

    List<Booking> findByHotelIdAndCheckInDateBetweenOrCheckOutDateBetween(
            Long roomId, LocalDate checkInStart, LocalDate checkInEnd, LocalDate checkOutStart, LocalDate checkOutEnd
    );

//    @Query("SELECT DISTINCT rb.room.roomNumber FROM Booking b " +
//            "JOIN b.bookedRooms rb " +
//            "WHERE b.hotel.id = :hotelId " +
//            "AND (:checkInDate BETWEEN b.checkInDate AND b.checkOutDate " +
//            "   OR :checkOutDate BETWEEN b.checkInDate AND b.checkOutDate)")
//    List<String> findBookedRoomNumbers(
//            @Param("hotelId") Long hotelId,
//            @Param("checkInDate") LocalDate checkInDate,
//            @Param("checkOutDate") LocalDate checkOutDate);

    @Query("SELECT DISTINCT rb.room.roomNumber FROM Booking b " +
            "JOIN b.bookedRooms rb " +
            "WHERE b.hotel.id = :hotelId " +
            "AND (:checkInDate < b.checkOutDate AND :checkOutDate > b.checkInDate)" +
            "AND b.status = 'CONFIRMED'")
            List<String> findBookedRoomNumbers(
            @Param("hotelId") Long hotelId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);

    List<Booking> findByCheckInDate(LocalDate checkinDate);

    List<Booking> findByCheckOutDate(LocalDate checkoutDate);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.hotel.id = :hotelId " +
            "AND (b.checkInDate <= :endDate AND b.checkOutDate >= :startDate) ORDER BY b.checkInDate ASC")
    List<Booking> findBookingsByHotelAndDateRange(Long hotelId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.hotel.id = :hotelId " +
            "AND b.checkOutDate >= CURRENT_DATE " +
            "ORDER BY b.checkInDate ASC")
    List<Booking> findOnwardBookings(@Param("hotelId") Long hotelId);

    Booking getBookingByBookingNumber(String bookingNumber);
}
