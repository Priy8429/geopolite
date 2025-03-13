package com.priyhotel.service;

import com.priyhotel.entity.Booking;
import com.priyhotel.entity.Payment;
import com.priyhotel.entity.User;
import com.priyhotel.repository.BookingRepository;
import com.priyhotel.repository.PaymentRepository;
import com.priyhotel.repository.UserRepository;
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

@Service
public class PaymentService {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    @Value("${razorpay.currency}")
    private String currency;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PDFGenerator pdfGenerator;

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    public PaymentService(PaymentRepository paymentRepository, BookingRepository bookingRepository, EmailService emailService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
    }

    public String createOrder(double amount, Long bookingId) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);

        // Link order with a booking
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        double validatedAmount = calculateAmount(booking.get());

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (amount * 100)); // Amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "txn_123456");

        Order order = razorpay.orders.create(orderRequest);

        Payment payment = new Payment();
        payment.setRazorpayOrderId(order.get("id"));
        payment.setAmount(amount);
        payment.setStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setBooking(booking.get());
        paymentRepository.save(payment);
        return order.toString();
    }

    // Verify and Save Payment
    public boolean verifyAndSavePayment(String paymentId, String orderId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            String generatedSignature = HmacSHA256(payload, apiSecret);

            if (generatedSignature.equals(signature)) {
                Payment payment = paymentRepository.findByRazorpayOrderId(orderId)
                        .orElseThrow(() -> new RuntimeException("Payment not found"));

                payment.setRazorpayPaymentId(paymentId);
                payment.setStatus("SUCCESS");
                payment.setPaymentDate(LocalDateTime.now());
                paymentRepository.save(payment);

                // Generate PDF Invoice
                byte[] pdfInvoice = pdfGenerator.generateInvoice(payment);

                // Send Payment Confirmation Email
                sendPaymentConfirmationEmail(payment, pdfInvoice);
                return true;
            }
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

    private void sendPaymentConfirmationEmail(Payment payment, byte[] pdfInvoice) {
        User user = payment.getBooking().getUser();
        String email = user.getEmail();
        String subject = "Payment Confirmation - Booking #" + payment.getBooking().getId();

        String content = "<h2>Dear " + user.getName() + ",</h2>"
                + "<p>Your payment of <strong>₹" + payment.getAmount() + "</strong> has been successfully received.</p>"
                + "<p><strong>Booking ID:</strong> " + payment.getBooking().getId() + "</p>"
                + "<p><strong>Payment ID:</strong> " + payment.getRazorpayPaymentId() + "</p>"
                + "<p><strong>Status:</strong> " + payment.getStatus() + "</p>"
                + "<p>Thank you for choosing our hotel!</p>"
                + "<br>"
                + "<p>Best Regards,</p>"
                + "<p>Hotel Management</p>";

        emailService.sendPaymentConfirmation(email, subject, content, pdfInvoice);
    }

    private double calculateAmount(Booking booking) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        return booking.getRoom().getRoomType().getPricePerNight() * days;
    }

}
