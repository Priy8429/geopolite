package com.priyhotel.controller;

import com.priyhotel.constants.PaymentType;
import com.priyhotel.dto.*;
import com.priyhotel.entity.Booking;
import com.priyhotel.entity.Payment;
import com.priyhotel.entity.Room;
import com.priyhotel.mapper.BookingMapper;
import com.priyhotel.service.BookingService;
import com.razorpay.Refund;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    BookingMapper bookingMapper;

    Logger logger = LoggerFactory.getLogger(BookingController.class);

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDto bookingRequestDto) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bookingMapper.toResponseDto(bookingService.createBooking(bookingRequestDto)));
        }catch(Exception ex){
            logger.error("Some error occurred: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(DefaultErrorResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(ex.getMessage())
                    .build());
        }
    }

    @PostMapping("/guest")
    public ResponseEntity<?> createBookingForGuest(@RequestBody GuestBookingRequestDto bookingRequestDto) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bookingService.createBookingForGuest(bookingRequestDto));
        }catch(Exception ex){
            logger.error("Some error occurred: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(DefaultErrorResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(ex.getMessage())
                    .build());
        }
    }

    @PostMapping("/offline")
    public ResponseEntity<?> createBookingForOfflineGuest(@RequestBody GuestBookingRequestDto bookingRequestDto) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bookingService.createBookingForOfflineGuest(bookingRequestDto));
        }catch(Exception ex){
            logger.error("Some error occurred: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(DefaultErrorResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(ex.getMessage())
                    .build());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponseDto>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingResponseById(id));
    }

    @PutMapping("/{bookingNumber}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable String bookingNumber) {
        try{
            BookingResponseDto response = bookingService.cancelBooking(bookingNumber);
            if(response.getPaymentType().equals(PaymentType.PREPAID)){
                return ResponseEntity.ok("Your booking is cancelled and refund has been initiated!");
            }else{
                return ResponseEntity.ok("Your booking has been cancelled!");
            }

        }catch (Exception ex){
            logger.error("Some error occurred: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(DefaultErrorResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(ex.getMessage())
                    .build());
        }

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
    public ResponseEntity<?> getBookingsByHotelAndDateRange(@RequestParam Long hotelId,
                                                         @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
                                                         @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate){
        return ResponseEntity.ok(bookingService.getBookingsByHotelAndDateRange(hotelId, startDate, endDate));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<?> getOnwardBookings(@RequestParam Long hotelId, @RequestParam(required = false) Integer pageNumber, @RequestParam(required = false) Integer pageSize){
        return ResponseEntity.ok(bookingService.getOnwardBookings(hotelId, pageNumber, pageSize));
    }

    @PatchMapping("/update-checkout")
    public ResponseEntity<?> updateCheckoutDate(@RequestBody UpdateCheckoutRequestDto requestDto){
        return ResponseEntity.ok(bookingService.updateCheckout(requestDto));
    }

    @PostMapping("/{bookingNumber}/payment/update-status/offline")
    public ResponseEntity<?> updateBookingPaymentStatusForOfflinePayment(@PathVariable String bookingNumber){
        Payment payment = bookingService.updateStatusForOfflinePayment(bookingNumber);
        if(payment != null){
            return ResponseEntity.ok(payment);
        }else{
            return ResponseEntity.ok("Issue in saving payment!");
        }

    }

}
