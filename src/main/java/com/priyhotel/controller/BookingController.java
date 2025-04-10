package com.priyhotel.controller;

import com.priyhotel.dto.BookingRequestDto;
import com.priyhotel.dto.BookingRequestQueryDto;
import com.priyhotel.dto.DefaultErrorResponse;
import com.priyhotel.entity.Booking;
import com.priyhotel.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    Logger logger = LoggerFactory.getLogger(BookingController.class);

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDto bookingRequestDto) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bookingService.createBooking(bookingRequestDto));
        }catch(Exception ex){
            logger.error("Some error occurred: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(DefaultErrorResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(ex.getMessage())
                    .build());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PostMapping("/request")
    public ResponseEntity<?> createBookingRequest(@RequestBody BookingRequestQueryDto bookingRequestQueryDto){
        return ResponseEntity.ok(bookingService.createBookingRequest(bookingRequestQueryDto));
    }

    @GetMapping("availability")
    public ResponseEntity<?> getRoomsAvailability(@RequestParam Long hotelId,
                                                  @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate checkinDate,
                                                  @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate checkoutDate){
        return ResponseEntity.ok(bookingService.getAvailableRoomsByDate(hotelId, checkinDate, checkoutDate));
    }

    @GetMapping("user-checkins")
    public ResponseEntity<?> getUserCheckinsByDate(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate checkinDate){
        return ResponseEntity.ok(bookingService.getUserCheckinsByDate(checkinDate));
    }

    @GetMapping("user-checkouts")
    public ResponseEntity<?> getUserCheckoutsByDate(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate checkoutDate){
        return ResponseEntity.ok(bookingService.getUserCheckoutsByDate(checkoutDate));
    }

    @GetMapping
    public ResponseEntity<?> getBookingsByHotelDateRange(@RequestParam Long hotelId,
                                                         @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
                                                         @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate){
        return ResponseEntity.ok(bookingService.getBookingsByHotelAndDateRange(hotelId, startDate, endDate));
    }
}
