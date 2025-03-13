package com.priyhotel.service;

import com.priyhotel.dto.BookingRequestDto;
import com.priyhotel.constants.BookingStatus;
import com.priyhotel.entity.Booking;
import com.priyhotel.entity.Coupon;
import com.priyhotel.entity.Room;
import com.priyhotel.entity.User;
import com.priyhotel.repository.BookingRepository;
import com.priyhotel.repository.RoomRepository;
import com.priyhotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CouponService couponService;

    public Booking createBooking(BookingRequestDto bookingRequestDto) {
//        Room room = roomRepository.findById(bookingRequestDto.getRoomId())
//                .orElseThrow(() -> new RuntimeException("Room not found"));

        Map<Long, Integer> roomTypeToQuantityMap = new HashMap<>();
        //getting the count of type of rooms required
        bookingRequestDto.getRoomBookingList()
                .forEach(roomBooking -> {
                    roomTypeToQuantityMap.merge(roomBooking.getRoomTypeId(), 1, Integer::sum);
                });



        // Check if the room is available for the selected dates
        List<Booking> existingBookings = bookingRepository.findByHotelIdAndCheckInDateBetweenOrCheckOutDateBetween(
                bookingRequestDto.getHotelId(), bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate(),
                bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate()
        );

        if (!existingBookings.isEmpty()) {
            throw new RuntimeException("Room is already booked for the selected dates.");
        }

        User user = userRepository.findById(bookingRequestDto.getUserId()).orElseThrow(
                () -> new RuntimeException("Invalid user"));

//        double totalPrice = (bookingRequestDto.getCheckOutDate().toEpochDay() - bookingRequestDto.getCheckInDate().toEpochDay()) * room.getRoomType().getPricePerNight();
//
//        if(!bookingRequestDto.getCouponCode().isBlank()){
//            Coupon coupon = couponService.getCouponByCouponCode(bookingRequestDto.getCouponCode());
//            totalPrice = couponService.applyDiscount(coupon,  totalPrice);
//        }

        Booking booking = new Booking();
        booking.setUser(user);
//        booking.setRoom(room);
        booking.setCheckInDate(bookingRequestDto.getCheckInDate());
        booking.setCheckOutDate(bookingRequestDto.getCheckOutDate());
//        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.PENDING);

        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public Booking cancelBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            booking.setStatus(BookingStatus.CANCELLED);
            Room room = booking.getRoom();
            room.setAvailable(true);  // Make room available again
            roomService.updateRoom(room.getId(), room);
        }
        return bookingRepository.save(booking);
    }
}
