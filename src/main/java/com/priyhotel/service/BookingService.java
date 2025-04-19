package com.priyhotel.service;

import com.priyhotel.constants.PaymentType;
import com.priyhotel.constants.BookingStatus;
import com.priyhotel.dto.*;
import com.priyhotel.entity.*;
import com.priyhotel.exception.BadRequestException;
import com.priyhotel.mapper.BookingMapper;
import com.priyhotel.mapper.RoomTypeMapper;
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

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private RoomTypeMapper roomTypeMapper;

    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {

        User user = authService.getUserById(bookingRequestDto.getUserId());
        Hotel hotel = hotelService.getHotelById(bookingRequestDto.getHotelId());

//        Map<Long, Integer> roomTypeToQuantityMap = new HashMap<>();
        //getting the count of type of rooms required
//        bookingRequestDto.getRoomBookingList()
//                .forEach(roomBooking -> {
//                    roomTypeToQuantityMap.merge(roomBooking.getRoomTypeId(), 1, Integer::sum);
//                });

        List<String> alreadyBookedRoomNumbers = bookingRepository.findBookedRoomNumbers(hotel.getId(), bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate());

        List<Room> availableRooms = roomService.findAvailableRoomsForRoomTypes(bookingRequestDto, alreadyBookedRoomNumbers);

        int requestedRoomsQty = bookingRequestDto.getRoomBookingList().stream().mapToInt(RoomBookingDto::getNoOfRooms).sum();

        if(availableRooms.isEmpty() || availableRooms.size() != requestedRoomsQty){
            throw new BadRequestException("Room/s not available!");
        }

//        double totalAmount = (bookingRequestDto.getCheckOutDate().toEpochDay() - bookingRequestDto.getCheckInDate().toEpochDay())
//                * availableRooms.stream().mapToDouble(item -> item.getRoomType().getPricePerNight()).sum();
//
//        double amountAfterDiscount = totalAmount;

        Booking booking = new Booking();

//        if(!bookingRequestDto.getCouponCode().isBlank()){
//            Coupon coupon = couponService.getCouponByCouponCode(bookingRequestDto.getCouponCode());
//            amountAfterDiscount = couponService.applyDiscount(coupon,  totalAmount);
//            booking.setCouponApplied(true);
//            booking.setCouponCode(bookingRequestDto.getCouponCode());
//        }

//        Double totalAmount = availableRooms.stream().mapToDouble(room -> room.getRoomType().getPricePerNight()).sum();

//        Double amountAfterDiscount = availableRooms.stream().mapToDouble(room ->
//                roomTypeService.getAmountAfterDiscount(room.getRoomType().getOfferStartDate(), room.getRoomType().getOfferEndDate(),
//                        room.getRoomType().getPricePerNight(), room.getRoomType().getOfferDiscountPercentage()))
//                .sum();

        booking.setBookingNumber(this.generateBookingNumber());
        booking.setUser(user);
        booking.setHotel(hotel);
        booking.setTotalRooms(availableRooms.size());
        booking.setNoOfAdults(bookingRequestDto.getNoOfAdults());
        booking.setNoOfChildrens(bookingRequestDto.getNoOfChildrens());
        booking.setCheckInDate(bookingRequestDto.getCheckInDate());
        booking.setCheckOutDate(bookingRequestDto.getCheckOutDate());
        booking.setTotalAmount(bookingRequestDto.getTotalAmount());
//        booking.setPaymentType(PAy);
        booking.setPayableAmount(bookingRequestDto.getPayableAmount());
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

        return bookingMapper.toResponseDto(savedBooking);
    }

    public List<BookingResponseDto> getUserBookings(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return bookingMapper.toResponseDtos(bookings);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public BookingResponseDto getBookingResponseById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return bookingMapper.toResponseDto(booking);
    }

    public BookingResponseDto cancelBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            booking.setStatus(BookingStatus.CANCELLED);
        }
        bookingRepository.save(booking);
        return bookingMapper.toResponseDto(booking);
    }

//    public double calculateBookingAmount(LocalDate checkinDate, LocalDate checkoutDate, List<RoomBooking> rooms, String couponCode){
//        long noOfDays = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
//
//        double totalRoomsCost = rooms.stream().mapToDouble(room -> room.getRoom().getRoomType().getPricePerNight()).sum();
//
//        double totalPrice = totalRoomsCost * noOfDays;
//        if(Objects.nonNull(couponCode) && !couponCode.isBlank()){
//            Coupon coupon = couponService.getCouponByCouponCode(couponCode);
//            totalPrice = couponService.applyDiscount(coupon,  totalPrice);
//        }
//
//        return totalPrice;
//    }

    public Double calculateBookingAmount(LocalDate checkinDate, LocalDate checkoutDate, List<RoomBooking> rooms){
        long noOfDays = ChronoUnit.DAYS.between(checkinDate, checkoutDate);

        Double totalAmount = rooms.stream().mapToDouble(room -> roomTypeService.getAmountAfterDiscount(
                room.getRoom().getRoomType().getOfferStartDate(), room.getRoom().getRoomType().getOfferEndDate(),
                room.getRoom().getRoomType().getPricePerNight(), room.getRoom().getRoomType().getOfferDiscountPercentage()
        )).sum();
        return totalAmount * noOfDays;
    }

    private void saveBookedRooms(List<RoomBooking> bookedRooms){
        roomBookingRepository.saveAll(bookedRooms);
    }

    private String generateBookingNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    public String createBookingRequest(BookingRequestQueryDto bookingRequestQueryDto) {
        Hotel hotel = hotelService.getHotelById(bookingRequestQueryDto.getHotelId());
        emailService.sendBookingQueryMailToHotelOwner(hotel.getEmail(), bookingRequestQueryDto);
        return "Your request has been sent successfully!";
    }

    public List<RoomTypeAvailabilityResponse> getAvailableRoomsByDate(Long hotelId, LocalDate checkinDate, LocalDate checkoutDate) {
        List<RoomType> roomTypes = hotelService.getHotelById(hotelId).getRoomTypes();
        List<RoomTypeAvailabilityResponse> rtDtos = roomTypeMapper.toRTAvailabilityDtos(roomTypes);
        List<String> alreadyBookedRoomNumbers = bookingRepository.findBookedRoomNumbers(hotelId, checkinDate, checkoutDate);
        Map<String, Long> roomsMap = roomService.findAvailableRoomsCountForRoomTypes(hotelId, alreadyBookedRoomNumbers);
        rtDtos.forEach(roomType -> {
            roomType.setAvailableRoomsQty(roomsMap.getOrDefault(roomType.getTypeName(), 0L));
        });
        return rtDtos;
    }

    public List<BookingResponseDto> getUserCheckinsByDate(LocalDate checkinDate) {
        List<Booking> bookings = bookingRepository.findByCheckInDate(checkinDate);
        return bookingMapper.toResponseDtos(bookings);

    }

    public List<BookingResponseDto> getUserCheckoutsByDate(LocalDate checkoutDate) {
        List<Booking> bookings = bookingRepository.findByCheckOutDate(checkoutDate);
        return bookingMapper.toResponseDtos(bookings);

    }

    public List<BookingResponseDto> getBookingsByHotelAndDateRange(Long hotelId, LocalDate startDate, LocalDate endDate) {
        if(Objects.isNull(startDate)){
            startDate = LocalDate.now();
        }
        if(Objects.isNull(endDate)){
            endDate = LocalDate.now().plusMonths(6);
        }

        List<Booking> bookings = bookingRepository.findBookingsByHotelAndDateRange(hotelId, startDate, endDate);
        return bookingMapper.toResponseDtos(bookings);

    }

    public List<BookingResponseDto> getOnwardBookings(Long hotelId) {
        List<Booking> bookings = bookingRepository.findOnwardBookings(hotelId);
        return bookingMapper.toResponseDtos(bookings);
    }

    public Booking getBookingByBookingNumber(String bookingNumber) {
        return bookingRepository.getBookingByBookingNumber(bookingNumber);
    }

    public BookingResponseDto updateCheckoutDate(Long bookingId, LocalDate newCheckoutDate) {
        Booking booking = this.getBookingById(bookingId);
        booking.setCheckOutDate(newCheckoutDate);
        bookingRepository.save(booking);
        return bookingMapper.toResponseDto(booking);
    }
}
