package com.priyhotel.service;

import com.itextpdf.text.pdf.PdfPTable;
import com.priyhotel.dto.PaymentVerifyRequestDto;
import com.priyhotel.entity.*;
import com.priyhotel.exception.BadRequestException;
import com.priyhotel.exception.ResourceNotFoundException;
import com.priyhotel.repository.PaymentRepository;
import com.priyhotel.util.PDFGenerator;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

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

    public String createOrder(String bookingNumber) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);

        // Link order with a booking
        Booking booking = bookingService.getBookingByBookingNumber(bookingNumber);

        double validatedAmount = bookingService.calculateBookingAmount(
                booking.getCheckInDate(), booking.getCheckOutDate(), booking.getBookedRooms());

        if(booking.getTotalAmount() != validatedAmount){
            throw new BadRequestException("Discrepancy in booking amount");
        }

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (booking.getTotalAmount() * 100)); // Amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "txn_123456");

//        Order order = razorpay.orders.create(orderRequest);

        Payment payment = new Payment();
//        payment.setRazorpayOrderId(order.get("id"));
        payment.setRazorpayOrderId(UUID.randomUUID().toString());
        payment.setRazorpayPaymentId(UUID.randomUUID().toString());
        payment.setAmount(booking.getTotalAmount());
        payment.setStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setBooking(booking);
        paymentRepository.save(payment);
//        return order.toString();
        return "order created";
    }

    // Verify and Save Payment
    public boolean verifyAndSavePayment(PaymentVerifyRequestDto paymentVerifyRequestDto) {
        try {
            String payload = paymentVerifyRequestDto.getOrderId() + "|" + paymentVerifyRequestDto.getPaymentId();
            String generatedSignature = HmacSHA256(payload, apiSecret);

//            if (generatedSignature.equals(signature)) {
                Payment payment = paymentRepository.findByRazorpayOrderId(paymentVerifyRequestDto.getOrderId())
                        .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

                payment.setRazorpayPaymentId(paymentVerifyRequestDto.getPaymentId());
                payment.setStatus("SUCCESS");
                payment.setPaymentDate(LocalDateTime.now());
                paymentRepository.save(payment);

                // Send Payment Confirmation Email
                emailService.sendPaymentConfirmationEmailToUser(payment.getBooking(), payment);
                emailService.sendPaymentConfirmationEmailToOwner(payment.getBooking(), payment);

                // Send payment confirmation sms
//                smsService.sendPaymentConfirmationSmsToUser(payment);
//                  smsService.sendPaymentConfirmationSmsToOwner(payment);
                return true;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // HMAC SHA256 Helper Method
    private String HmacSHA256(String data, String key) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        return Base64.getEncoder().encodeToString(sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

}
