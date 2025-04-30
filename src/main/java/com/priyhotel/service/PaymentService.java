package com.priyhotel.service;

import com.priyhotel.constants.BookingStatus;
import com.priyhotel.dto.PaymentVerifyRequestDto;
import com.priyhotel.dto.RoomBookingDto;
import com.priyhotel.entity.Booking;
import com.priyhotel.entity.Payment;
import com.priyhotel.entity.RoomBooking;
import com.priyhotel.entity.RoomBookingRequest;
import com.priyhotel.exception.ResourceNotFoundException;
import com.priyhotel.mapper.RoomBookingMapper;
import com.priyhotel.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class PaymentService {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    @Value("${razorpay.currency}")
    private String currency;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    BookingService bookingService;

    @Autowired
    EmailService emailService;

    @Autowired
    RoomBookingMapper roomBookingMapper;

    @Transactional
    public String createOrder(String bookingNumber) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);

        // Link order with a booking
        Booking booking = bookingService.getBookingByBookingNumber(bookingNumber);

//        double validatedAmount = bookingService.calculateBookingAmount(
//                booking.getCheckInDate(), booking.getCheckOutDate(), booking.getBookedRooms());
//
//        if(booking.getTotalAmount() != validatedAmount){
//            throw new BadRequestException("Discrepancy in booking amount");
//        }

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (booking.getPayableAmount() * 100)); // Amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "txn_123456");

        Order order = razorpay.orders.create(orderRequest);

        Payment payment = new Payment();
        payment.setRazorpayOrderId(order.get("id"));
//        payment.setRazorpayOrderId(UUID.randomUUID().toString());
//        payment.setRazorpayPaymentId(UUID.randomUUID().toString());
        payment.setAmount(booking.getTotalAmount());
        payment.setStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setBooking(booking);
        paymentRepository.save(payment);

        // reserve rooms
//        List<RoomBookingRequest> roomBookingRequestList = bookingService.getBookingRoomRequestsByBookingId(booking.getId());
//        List<RoomBookingDto> roomBookingDtos = roomBookingMapper.toDtos(roomBookingRequestList);
//        List<RoomBooking> bookedRooms = bookingService.bookRooms(
//                bookingService.getAvailableRooms(booking.getHotel().getId(), booking.getCheckInDate(), booking.getCheckOutDate(),roomBookingDtos)
//                , booking
//        );
//        booking.setBookedRooms(bookedRooms);
//        bookingService.saveBooking(booking);

        bookingService.reserveRooms(booking);

        return order.toString();
//        return "order created";
    }

    // Verify and Save Payment
    @Transactional
    public Booking verifyAndSavePayment(PaymentVerifyRequestDto paymentVerifyRequestDto) {
        try {
            String payload = paymentVerifyRequestDto.getOrderId() + "|" + paymentVerifyRequestDto.getPaymentId();
//            String generatedSignature = HmacSHA256(payload, apiSecret);
            Utils.verifySignature(payload, paymentVerifyRequestDto.getSignature(), apiSecret);
//            if (generatedSignature.equals(signature)) {
                Payment payment = paymentRepository.findByRazorpayOrderId(paymentVerifyRequestDto.getOrderId())
                        .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

                payment.setRazorpayPaymentId(paymentVerifyRequestDto.getPaymentId());
                payment.setStatus("SUCCESS");
                payment.setPaymentDate(LocalDateTime.now());
                paymentRepository.save(payment);

                //update booking status
                bookingService.updateBookingStatus(payment.getBooking().getBookingNumber(), BookingStatus.CONFIRMED);

                // Send Payment Confirmation Email
                emailService.sendPaymentConfirmationEmailToUser(payment.getBooking(), payment);
                emailService.sendPaymentConfirmationEmailToOwner(payment.getBooking(), payment);

                // Send payment confirmation sms
//                smsService.sendPaymentConfirmationSmsToUser(payment);
//                  smsService.sendPaymentConfirmationSmsToOwner(payment);
                return payment.getBooking();
//            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void handlePaymentFailure(String orderId){
        Payment payment = paymentRepository.findByRazorpayOrderId(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Payment  not found with orderId: " + orderId));
        bookingService.removeBookedRooms(payment.getBooking());
    }

    // HMAC SHA256 Helper Method
    private String HmacSHA256(String data, String key) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        return Base64.getEncoder().encodeToString(sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

}
