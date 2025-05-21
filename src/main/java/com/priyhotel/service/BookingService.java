package com.priyhotel.service;

import com.priyhotel.constants.PaymentType;
import com.priyhotel.constants.BookingStatus;
import com.priyhotel.constants.Role;
import com.priyhotel.dto.*;
import com.priyhotel.entity.*;
import com.priyhotel.exception.BadRequestException;
import com.priyhotel.mapper.BookingMapper;
import com.priyhotel.mapper.RoomBookingMapper;
import com.priyhotel.mapper.RoomTypeMapper;
import com.priyhotel.repository.BookingRepository;
import com.priyhotel.repository.RoomBookingRepository;
import com.priyhotel.repository.RoomBookingRequestRepository;
import com.razorpay.RazorpayException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private RoomBookingRequestRepository roomBookingRequestRepository;

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


    private final PaymentService paymentService;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private RoomTypeMapper roomTypeMapper;

    @Autowired
    private RoomBookingMapper roomBookingMapper;

    public BookingService(@Lazy PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @Transactional
    public Booking createBooking(BookingRequestDto bookingRequestDto) {

        User user = authService.getUserByIdCanNull(bookingRequestDto.getUserId());
        Hotel hotel = hotelService.getHotelById(bookingRequestDto.getHotelId());

//        Map<Long, Integer> roomTypeToQuantityMap = new HashMap<>();
        //getting the count of type of rooms required
//        bookingRequestDto.getRoomBookingList()
//                .forEach(roomBooking -> {
//                    roomTypeToQuantityMap.merge(roomBooking.getRoomTypeId(), 1, Integer::sum);
//                });

//        List<String> alreadyBookedRoomNumbers = bookingRepository.findBookedRoomNumbers(hotel.getId(), bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate());

//        List<Room> availableRooms = roomService.findAvailableRoomsForRoomTypes(bookingRequestDto.getHotelId(), bookingRequestDto.getRoomBookingList(), alreadyBookedRoomNumbers);

        List<Room> availableRooms = this.getAvailableRooms(bookingRequestDto.getHotelId(), bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate(), bookingRequestDto.getRoomBookingList());
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
        booking.setPayableAmount(bookingRequestDto.getPayableAmount());
        booking.setStatus(BookingStatus.PENDING);

        if(Objects.isNull(bookingRequestDto.getPaymentType())){
            booking.setPaymentType(PaymentType.POSTPAID);
        }else{
            booking.setPaymentType(bookingRequestDto.getPaymentType());
        }

        Booking savedBooking = bookingRepository.save(booking);

        // save room booking requests
        List<RoomBookingRequest> roomBookingRequests = roomBookingMapper.toEntities(bookingRequestDto.getRoomBookingList(), booking);
        roomBookingRequestRepository.saveAll(roomBookingRequests);

        if(booking.getPaymentType().equals(PaymentType.POSTPAID)){
            booking.setStatus(BookingStatus.CONFIRMED);
            List<RoomBooking> roomBookings = this.bookRooms(availableRooms, savedBooking);
            booking.setBookedRooms(roomBookings);
            saveBookedRooms(roomBookings);
        }

        bookingRepository.save(booking);

        // if payment is postpaid, send confirmation mail and sms to user and owner
        if(booking.getPaymentType().equals(PaymentType.POSTPAID)){
            emailService.sendPaymentConfirmationEmailToUser(booking, null);
            emailService.sendPaymentConfirmationEmailToOwner(booking, null);
        }

//        return bookingMapper.toResponseDto(savedBooking);
        return savedBooking;
    }

    public String createBookingForGuest(GuestBookingRequestDto guestBookingRequestDto) throws RazorpayException {
        User user = null;

        Optional<User> userByEmail = authService.findByEmail(guestBookingRequestDto.getEmail());
        Optional<User> userByContactNumber = authService.findByPhoneNumber(guestBookingRequestDto.getPhone());
        if(userByContactNumber.isPresent()){
            user = userByContactNumber.get();
        }

        if(userByEmail.isPresent()){
            user = userByEmail.get();
        }

        if(userByEmail.isEmpty() && userByContactNumber.isEmpty()){
            UserRequestDto guestUser = UserRequestDto.builder()
                    .name(guestBookingRequestDto.getFullName())
                    .email(guestBookingRequestDto.getEmail())
                    .contactNumber(guestBookingRequestDto.getPhone())
                    .role(Role.GUEST).build();
            user = authService.register(guestUser);
        }

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .userId(user.getId()) // null if guest
                .hotelId(guestBookingRequestDto.getHotelId())
                .couponCode("")
                .noOfAdults(guestBookingRequestDto.getNoOfAdults())
                .noOfChildrens(guestBookingRequestDto.getNoOfChildrens())
                .checkInDate(guestBookingRequestDto.getCheckInDate())
                .checkOutDate(guestBookingRequestDto.getCheckOutDate())
                .paymentType(PaymentType.PREPAID)
                .totalAmount(guestBookingRequestDto.getTotalAmount())
                .payableAmount(guestBookingRequestDto.getPayableAmount())
                .roomBookingList(guestBookingRequestDto.getRoomBookingList()) // List<RoomBookingDto>
                .build();
        Booking booking = this.createBooking(bookingDto);
//        this.reserveRooms(booking);
        return paymentService.createOrder(booking.getBookingNumber());
    }

    public List<RoomBooking> bookRooms(List<Room> availableRooms, Booking booking){
        List<RoomBooking> roomBookings = new ArrayList<>();
        List<RoomBookingRequest> roomsList = roomBookingRequestRepository.findByBookingId(booking.getId());

        for(Room room: availableRooms){
            Optional<RoomBookingRequest> roomBookingDto = roomsList.stream().filter(dto -> dto.getRoomTypeId().equals(room.getRoomType().getId())).findFirst();
            if(roomBookingDto.isPresent()){
                RoomBooking roomBooking = new RoomBooking();
                roomBooking.setRoom(room);
                roomBooking.setBooking(booking);
                roomBooking.setNoOfNights((int)(booking.getCheckOutDate().toEpochDay() - booking.getCheckInDate().toEpochDay()));

                // add room to the final list
                roomBookings.add(roomBooking);
                if(roomBookingDto.get().getNoOfRooms() == 0){
                    // remove added room from the list
                    roomsList.remove(roomBookingDto.get());
                }else{
                    // decrease the count of room alotted
                    roomBookingDto.get().setNoOfRooms(roomBookingDto.get().getNoOfRooms()-1);
                }
            }
        }
        return roomBookings;
    }

    public List<Room> getAvailableRooms(Long hotelId, LocalDate checkinDate, LocalDate checkoutDate, List<RoomBookingDto> roomBookingDtos){
        List<String> alreadyBookedRoomNumbers = bookingRepository.findBookedRoomNumbers(hotelId, checkinDate, checkoutDate);
        return roomService.findAvailableRoomsForRoomTypes(hotelId, roomBookingDtos, alreadyBookedRoomNumbers);
    }

    public List<String> getAlreadyBookedRoomNumbers(Long hotelId, LocalDate checkinDate, LocalDate checkoutDate){
        return bookingRepository.findBookedRoomNumbers(hotelId, checkinDate, checkoutDate);
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

    public BookingResponseDto updateCheckoutDate(String bookingNumber, LocalDate newCheckoutDate) {
        Booking booking = this.getBookingByBookingNumber(bookingNumber);
        booking.setCheckOutDate(newCheckoutDate);
        bookingRepository.save(booking);
        return bookingMapper.toResponseDto(booking);
    }

    public void updateBookingStatus(String bookingNumber, BookingStatus status){
        Booking booking = this.getBookingByBookingNumber(bookingNumber);
        booking.setStatus(status);
        bookingRepository.save(booking);
    }

    public List<RoomBookingRequest> getBookingRoomRequestsByBookingId(Long bookingId){
        return this.roomBookingRequestRepository.findByBookingId(bookingId);
    }

    public Booking saveBooking(Booking booking){
        return bookingRepository.save(booking);
    }

    public void removeBookedRooms(Booking booking){
        roomBookingRepository.deleteAll(booking.getBookedRooms());
    }

    public void reserveRooms(Booking booking){
        // reserve rooms
        List<RoomBookingRequest> roomBookingRequestList = this.getBookingRoomRequestsByBookingId(booking.getId());
        List<RoomBookingDto> roomBookingDtos = roomBookingMapper.toDtos(roomBookingRequestList);
        List<RoomBooking> bookedRooms = this.bookRooms(
                this.getAvailableRooms(booking.getHotel().getId(), booking.getCheckInDate(), booking.getCheckOutDate(),roomBookingDtos)
                , booking
        );
        booking.setBookedRooms(bookedRooms);
        this.saveBooking(booking);
    }
}
