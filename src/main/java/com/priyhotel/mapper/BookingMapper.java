package com.priyhotel.mapper;

import com.priyhotel.constants.PaymentStatus;
import com.priyhotel.dto.BookingDto;
import com.priyhotel.dto.BookingResponseDto;
import com.priyhotel.entity.Booking;
import com.priyhotel.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class BookingMapper {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RoomMapper roomMapper;

    public BookingDto toDto(Booking booking){
        return BookingDto.builder()
                .bookingNumber(booking.getBookingNumber())
                .user(userMapper.toDto(booking.getUser()))
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalAmount(booking.getTotalAmount())
                .couponApplied(booking.getCouponApplied())
                .couponCode(booking.getCouponCode())
                .payableAmount(booking.getPayableAmount())
                .totalRooms(booking.getTotalRooms())
                .noOfAdults(booking.getNoOfAdults())
                .noOfChildrens(booking.getNoOfChildrens())
                .bookedRooms(booking.getBookedRooms())
                .paymentType(booking.getPaymentType())
                .bookingSource(booking.getBookingSource())
                .specialRequest(booking.getSpecialRequest())
                .status(booking.getStatus())
                .build();
    }

    public List<BookingDto> toDtos(List<Booking> bookings){
        return bookings.stream().map(this::toDto).toList();
    }

    public BookingResponseDto toResponseDto(Booking booking){
        Optional<Payment> paidPayment = booking.getPayments().stream().filter(p -> p.getStatus().equals(PaymentStatus.PAID)).findFirst();
        return BookingResponseDto.builder()
                .bookingNumber(booking.getBookingNumber())
                .user(userMapper.toDto(booking.getUser()))
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalAmount(booking.getTotalAmount())
                .payableAmount(booking.getPayableAmount())
                .totalRooms(booking.getTotalRooms())
                .noOfAdults(booking.getNoOfAdults())
                .noOfChildrens(booking.getNoOfChildrens())
                .rooms(booking.getBookedRooms() != null ? roomMapper.toDtosFromRoomBooking(booking.getBookedRooms()) : null)
                .paymentType(booking.getPaymentType())
                .bookingSource(booking.getBookingSource())
                .specialRequest(booking.getSpecialRequest())
                .status(booking.getStatus())
                .paymentStatus(paidPayment.map(Payment::getStatus).orElse(null))
                .paymentAmount(paidPayment.map(Payment::getAmount).orElse(null))
                .paymentDate(paidPayment.map(p -> LocalDate.from(p.getPaymentDate())).orElse(null))
                .build();
    }

    public List<BookingResponseDto> toResponseDtos(List<Booking> bookings){
        return bookings.stream().map(this::toResponseDto).toList();
    }
}
