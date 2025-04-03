package com.priyhotel.service;

import com.priyhotel.constants.PaymentType;
import com.priyhotel.constants.BookingStatus;
import com.priyhotel.dto.BookingRequestDto;
import com.priyhotel.dto.RoomBookingDto;
import com.priyhotel.entity.*;
import com.priyhotel.exception.BadRequestException;
import com.priyhotel.repository.BookingRepository;
import com.priyhotel.repository.RoomBookingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomBookingRepository roomBookingRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private AuthService authService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RoomTypeService roomTypeService;

    @Transactional
    public Booking createBooking(BookingRequestDto bookingRequestDto) {

        User user = authService.getUserById(bookingRequestDto.getUserId());
        Hotel hotel = hotelService.getHotelById(bookingRequestDto.getHotelId());

        Map<Long, Integer> roomTypeToQuantityMap = new HashMap<>();
        //getting the count of type of rooms required
        bookingRequestDto.getRoomBookingList()
                .forEach(roomBooking -> {
                    roomTypeToQuantityMap.merge(roomBooking.getRoomTypeId(), 1, Integer::sum);
                });

        // get the already booked room numbers for the particular dates
        List<String> alreadyBookedRooms = bookingRepository.findByHotelIdAndCheckInDateBetweenOrCheckOutDateBetween(
                bookingRequestDto.getHotelId(), bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate(),
                bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate()
        )
                .stream().flatMap(booking -> booking.getBookedRooms().stream())
                .map(roomBooking -> roomBooking.getRoom().getRoomNumber())
                .toList();

        List<Room> availableRooms = roomService.findAvailableRoomsForRoomTypes(bookingRequestDto.getHotelId(), roomTypeToQuantityMap, alreadyBookedRooms);

        if(availableRooms.isEmpty() || availableRooms.size() != bookingRequestDto.getRoomBookingList().size()){
            throw new BadRequestException("Room/s not available!");
        }

        double totalAmount = (bookingRequestDto.getCheckOutDate().toEpochDay() - bookingRequestDto.getCheckInDate().toEpochDay())
                * availableRooms.stream().mapToDouble(item -> item.getRoomType().getPricePerNight()).sum();

        double amountAfterDiscount = totalAmount;

        Booking booking = new Booking();

        if(!bookingRequestDto.getCouponCode().isBlank()){
            Coupon coupon = couponService.getCouponByCouponCode(bookingRequestDto.getCouponCode());
            amountAfterDiscount = couponService.applyDiscount(coupon,  totalAmount);
            booking.setCouponApplied(true);
            booking.setCouponCode(bookingRequestDto.getCouponCode());
        }

        booking.setBookingNumber(this.generateBookingNumber());
        booking.setUser(user);
        booking.setHotel(hotel);
        booking.setTotalRooms(availableRooms.size());
        booking.setCheckInDate(bookingRequestDto.getCheckInDate());
        booking.setCheckOutDate(bookingRequestDto.getCheckOutDate());
        booking.setTotalAmount(amountAfterDiscount);
//        booking.setPaymentType(PAy);
        booking.setDiscountAmount(totalAmount - amountAfterDiscount);
        booking.setStatus(BookingStatus.PENDING);

        if(Objects.isNull(booking.getPaymentType())){
            booking.setPaymentType(PaymentType.POSTPAID);
        }else{
            booking.setPaymentType(booking.getPaymentType());
        }

        List<RoomBooking> roomBookings = new ArrayList<>();
        List<RoomBookingDto> roomsList = bookingRequestDto.getRoomBookingList();

        for(Room room: availableRooms){
            Optional<RoomBookingDto> roomBookingDto = roomsList.stream().filter(dto -> dto.getRoomTypeId().equals(room.getRoomType().getId())).findFirst();
            if(roomBookingDto.isPresent()){
                RoomBooking roomBooking = new RoomBooking();
                roomBooking.setRoom(room);
                roomBooking.setBooking(booking);
                roomBooking.setNoOfAdults(roomBooking.getNoOfAdults());
                roomBooking.setNoOfChilds(roomBookingDto.get().getNoOfChildrens());
                roomBooking.setNoOfNights((int)(bookingRequestDto.getCheckOutDate().toEpochDay() - bookingRequestDto.getCheckInDate().toEpochDay()));

                // add room to the final list
                roomBookings.add(roomBooking);
                // remove added room from the list
                roomsList.remove(roomBookingDto.get());
            }

        }

        if(booking.getPaymentType().equals(PaymentType.POSTPAID)){
            booking.setStatus(BookingStatus.CONFIRMED);
        }

        booking.setBookedRooms(roomBookings);

        Booking savedBooking = bookingRepository.save(booking);
        saveBookedRooms(roomBookings);

        // if payment is postpaid, send confirmation mail and sms to user and owner
        if(booking.getPaymentType().equals(PaymentType.POSTPAID)){
            emailService.sendPaymentConfirmationEmailToUser(booking, null);
            emailService.sendPaymentConfirmationEmailToOwner(booking, null);
        }

        return savedBooking;
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
        }
        return bookingRepository.save(booking);
    }

    public double calculateBookingAmount(LocalDate checkinDate, LocalDate checkoutDate, List<RoomBooking> rooms, String couponCode){
        Long noOfDays = ChronoUnit.DAYS.between(checkinDate, checkoutDate);

        double totalRoomsCost = rooms.stream().mapToDouble(room -> room.getRoom().getRoomType().getPricePerNight()).sum();

        double totalPrice = totalRoomsCost * noOfDays;
        if(Objects.nonNull(couponCode) && !couponCode.isBlank()){
            Coupon coupon = couponService.getCouponByCouponCode(couponCode);
            totalPrice = couponService.applyDiscount(coupon,  totalPrice);
        }
        
        return totalPrice;
    }

    private void saveBookedRooms(List<RoomBooking> bookedRooms){
        roomBookingRepository.saveAll(bookedRooms);
    }

    private String generateBookingNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

}
