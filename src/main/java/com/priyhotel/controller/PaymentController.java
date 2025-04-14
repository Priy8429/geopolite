package com.priyhotel.controller;

import com.priyhotel.dto.PaymentVerifyRequestDto;
import com.priyhotel.service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

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
    public ResponseEntity<String> verifyPayment(@RequestBody PaymentVerifyRequestDto paymentVerifyRequestDto) {
        boolean isVerified = paymentService.verifyAndSavePayment(paymentVerifyRequestDto);

        if (isVerified) {
            return ResponseEntity.ok("Payment verified successfully.");
        } else {
            return ResponseEntity.status(400).body("Payment verification failed.");
        }
    }

}
