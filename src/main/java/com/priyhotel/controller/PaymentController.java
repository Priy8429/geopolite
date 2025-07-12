package com.priyhotel.controller;

import com.priyhotel.dto.PaymentVerifyRequestDto;
import com.priyhotel.entity.Booking;
import com.priyhotel.mapper.BookingMapper;
import com.priyhotel.service.PaymentService;
import com.priyhotel.service.RefundService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    RefundService refundService;

    @Autowired
    BookingMapper bookingMapper;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestParam String bookingNumber) {
        try {
            String order = paymentService.createOrder(bookingNumber);
            return ResponseEntity.ok(order);
        } catch (RazorpayException e) {
            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerifyRequestDto paymentVerifyRequestDto) {
        Booking booking = paymentService.verifyAndSavePayment(paymentVerifyRequestDto);

        if (Objects.nonNull(booking)) {
            return ResponseEntity.ok(bookingMapper.toDto(booking));
        } else {
            return ResponseEntity.status(400).body("Payment verification failed.");
        }
    }

    @PostMapping("/failure")
    public ResponseEntity<?> handlePaymentFailure(@RequestParam String orderId){
        paymentService.handlePaymentFailure(orderId);
        return ResponseEntity.ok("Payment cancelled");
    }

    @GetMapping("/refund")
    public ResponseEntity<?> getRefundByPaymentId(@RequestParam String paymentId){
        try{
            return ResponseEntity.ok(refundService.getByPaymentId(paymentId));
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error getting refund: " + e.getMessage());
        }
    }

}
