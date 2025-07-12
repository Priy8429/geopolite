package com.priyhotel.service;

import com.priyhotel.constants.BookingStatus;
import com.priyhotel.constants.PaymentStatus;
import com.priyhotel.constants.PaymentType;
import com.priyhotel.constants.RefundStatus;
import com.priyhotel.dto.PaymentVerifyRequestDto;
import com.priyhotel.dto.RoomBookingDto;
import com.priyhotel.entity.Booking;
import com.priyhotel.entity.Payment;
import com.priyhotel.entity.RoomBooking;
import com.priyhotel.entity.RoomBookingRequest;
import com.priyhotel.exception.ResourceNotFoundException;
import com.priyhotel.mapper.RoomBookingMapper;
import com.priyhotel.repository.PaymentRepository;
import com.priyhotel.repository.RefundRepository;
import com.razorpay.*;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

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
    RefundRepository refundRepository;

    @Autowired
    BookingService bookingService;

    @Autowired
    EmailService emailService;

    @Autowired
    RoomBookingMapper roomBookingMapper;

    public RazorpayClient getRazorpayClient() throws RazorpayException {
        return new RazorpayClient(apiKey, apiSecret);
    }

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
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = razorpay.orders.create(orderRequest);

        Payment payment = new Payment();
        payment.setRazorpayOrderId(order.get("id"));
//        payment.setRazorpayOrderId(UUID.randomUUID().toString());
//        payment.setRazorpayPaymentId(UUID.randomUUID().toString());
        payment.setAmount(booking.getTotalAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setBooking(booking);
        payment.setCreatedOn(LocalDate.now());
        payment.setUpdatedOn(LocalDate.now());
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
                payment.setStatus(PaymentStatus.PAID);
                payment.setPaymentDate(LocalDateTime.now());
                payment.setUpdatedOn(LocalDate.now());
                paymentRepository.save(payment);



                //update booking status
                bookingService.updateBookingStatus(payment.getBooking().getBookingNumber(), BookingStatus.CONFIRMED);

                //for paylater option -> update payment type to prepaid
                Booking booking = payment.getBooking();
                booking.setPaymentType(PaymentType.PREPAID);
                booking.setUpdatedOn(LocalDate.now());
                bookingService.saveBooking(booking);

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

    public com.priyhotel.entity.Refund initiateRefund(Booking booking) throws RazorpayException {
        Optional<Payment> payment = paymentRepository.findByBooking(booking);
        if(payment.isPresent() && payment.get().getStatus().equals(PaymentStatus.PAID)){
            RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
            String paymentId = payment.get().getRazorpayPaymentId();
            System.out.println(razorpay.payments.fetch(payment.get().getRazorpayPaymentId()));
            long amount = Math.round(payment.get().getAmount()*100);
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount",amount-100);
            refundRequest.put("speed","normal");
            refundRequest.put("receipt", "ref_" + System.currentTimeMillis());
            System.out.println(paymentId);
            System.out.println(refundRequest);
            Refund razorpayRefund = razorpay.payments.refund(paymentId, refundRequest);

            com.priyhotel.entity.Refund refund = new com.priyhotel.entity.Refund();
            refund.setPayment(payment.get());
            refund.setRefundAmount(payment.get().getAmount());
            refund.setRefundId(razorpayRefund.get("id"));
            refund.setRefundStatus(RefundStatus.valueOf(razorpayRefund.get("status")));
            return refundRepository.save(refund);

        }else{
            throw new ResourceNotFoundException("Payment not found!");
        }

    }

    public void handlePaymentFailure(String orderId){
        Payment payment = paymentRepository.findByRazorpayOrderId(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Payment  not found with orderId: " + orderId));
        payment.setStatus(PaymentStatus.FAILED);
        payment.setUpdatedOn(LocalDate.now());
        paymentRepository.save(payment);
        bookingService.removeBookedRooms(payment.getBooking());
    }

    // HMAC SHA256 Helper Method
    private String HmacSHA256(String data, String key) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        return Base64.getEncoder().encodeToString(sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    public Payment getByPaymentId(String orderId){
        return paymentRepository.findByRazorpayPaymentId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }
}
