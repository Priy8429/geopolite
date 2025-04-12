package com.priyhotel.mapper;

import com.priyhotel.dto.BookingDto;
import com.priyhotel.entity.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingMapper {

    @Autowired
    UserMapper userMapper;

    public BookingDto toDto(Booking booking){
        return BookingDto.builder()
                .bookingNumber(booking.getBookingNumber())
                .user(userMapper.toDto(booking.getUser()))
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalAmount(booking.getTotalAmount())
                .couponApplied(booking.getCouponApplied())
                .couponCode(booking.getCouponCode())
                .discountAmount(booking.getDiscountAmount())
                .totalRooms(booking.getTotalRooms())
                .noOfAdults(booking.getNoOfAdults())
                .noOfChildrens(booking.getNoOfChildrens())
                .bookedRooms(booking.getBookedRooms())
                .paymentType(booking.getPaymentType())
                .status(booking.getStatus())
                .build();
    }

    public List<BookingDto> toDtos(List<Booking> bookings){
        return bookings.stream().map(this::toDto).toList();
    }
}
