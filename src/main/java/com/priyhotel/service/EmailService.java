package com.priyhotel.service;

import com.priyhotel.dto.BookingRequestQueryDto;
import com.priyhotel.entity.Booking;
import com.priyhotel.entity.Payment;
import com.priyhotel.entity.User;
import com.priyhotel.util.PDFGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    PDFGenerator pdfGenerator;

    @Value("${notification.destination.email}")
    private String sendToEmail;

//    @Value("${SMTP_PASS}")
//    private String smtpPass;

    private final String dateFormat = "dd-MM-yyyy";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);

    @Async
    public void sendPaymentConfirmationEmailToUser(Booking booking, Payment payment) {

        // Generate PDF Invoice
//        byte[] pdfInvoice = pdfGenerator.generateInvoice(booking, payment);
        User user = booking.getUser();
        String email = user.getEmail();
        String subject = "🛎️ Your Booking at Hotel Pride is Confirmed!";
        String roomNumbers = null;
        if(!booking.getBookedRooms().isEmpty()){
            roomNumbers = booking.getBookedRooms().stream().map(rb -> rb.getRoom().getRoomNumber()).collect(Collectors.joining(","));
        }
        StringBuilder content =  new StringBuilder();
        content.append("<p>Hi ").append(user.getName()).append("</p></br></br>")
                .append("<p>Thank you for choosing Hotel Pride for your upcoming stay in Mumbai! We're delighted to have the opportunity to host you.</p>").append("</br>")
                .append("<p><strong>Booking ID:</strong> ").append(booking.getBookingNumber()).append("</p>").append("</br>").append("</br>")
                .append("<p><strong>Payment mode:</strong> ").append(booking.getPaymentType()).append("</p>").append("</br>").append("</br>")
                .append("<p><strong>Room type:</strong> ").append(booking.getBookedRooms().get(0).getRoom().getRoomType().getTypeName()).append("</p>").append("</br>").append("</br>");
        if(Objects.nonNull(payment)){
            content.append("<p><strong>Amount paid: </strong>₹").append(payment.getAmount()).append("</p></br>")
                    .append("<p><strong>Payment ID: </strong> ").append(payment.getRazorpayPaymentId()).append("</p>")
                    .append("</br>").append("</br>");
        }
        if(Objects.nonNull(booking.getSpecialRequest())){
            content.append("<p><strong>Special request: </strong> ").append(booking.getSpecialRequest()).append("</p>").append("</br>");
        }
                content.append("<p><strong>Status: </strong> ").append(booking.getStatus()).append("</p>").append("</br>").append("</br>")
                .append("<p><strong>\uD83D\uDDD3\uFE0F Check-in:</strong> ").append(booking.getCheckInDate().format(dateFormatter)).append(" 12:00 PM").append("</p>").append("</br>")
                .append("<p><strong>\uD83D\uDDD3\uFE0F Check-out:</strong> ").append(booking.getCheckOutDate().format(dateFormatter)).append(" 11:00 AM").append("</p>").append("</br>");
        if(Objects.nonNull(roomNumbers)) {
            content.append("<p><strong>Room(s):</strong> ").append(roomNumbers).append("</p>").append("</br>");
        }
        content.append("<p><strong>No. of adults:</strong> ").append(booking.getNoOfAdults()).append("</p>").append("</br>")
                .append("<p><strong>No. of childrens:</strong> ").append(booking.getNoOfChildrens()).append("</p>").append("</br>")
                .append("<p>If you have any special requests or need assistance before your arrival, feel free to reply to this email or call us directly.</p>")
                .append("</br>").append("</br>")
                .append("<p>We can’t wait to welcome you</p>").append("</br>")
                .append("<p>Warm regards,</p>").append("</br>")
                .append("<p>Team Hotel Pride</p>").append("</br>")
                .append("<p>\uD83D\uDCDE 098199 14047</p>").append("</br>")
                .append("\uD83C\uDF10 <a href='http://hotelpride.com'>hotelpride.com</a>");


//        if(Objects.nonNull(payment)){
//            content.append("<p>Your payment of <strong>₹").append(payment.getAmount()).append("</strong> has been successfully received.</p>")
//                    .append("<p><strong>Payment ID:</strong> ").append(payment.getRazorpayPaymentId()).append("</p>");
//        }


        this.sendPaymentConfirmation(email, subject, content.toString());
    }

    @Async
    public void sendPaymentConfirmationEmailToOwner(Booking booking, Payment payment) {

        // Generate PDF Invoice
//        byte[] pdfInvoice = pdfGenerator.generateInvoice(booking, payment);

        String email = booking.getHotel().getEmail();
        String subject = "🛎️ Your Booking at Hotel Pride is Confirmed!";

        String roomNumbers = null;
        if(!booking.getBookedRooms().isEmpty()){
            roomNumbers = booking.getBookedRooms().stream().map(rb -> rb.getRoom().getRoomNumber()).collect(Collectors.joining(","));
        }

        // Construct the email body
        StringBuilder content =  new StringBuilder();
        content.append("<p>Customer booking confirmed!").append(booking.getUser().getName()).append("</p></br>")
                .append("<p><strong>Customer Name:</strong> ").append(booking.getUser().getName()).append("</p>").append("</br>").append("</br>")
                .append("<p><strong>Booking ID:</strong> ").append(booking.getBookingNumber()).append("</p>").append("</br>").append("</br>")
                .append("<p><strong>Payment mode:</strong> ").append(booking.getPaymentType()).append("</p>").append("</br>").append("</br>")
                .append("<p><strong>Room type:</strong> ").append(booking.getBookedRooms().get(0).getRoom().getRoomType().getTypeName()).append("</p>").append("</br>").append("</br>");

        if(Objects.nonNull(payment)){
            content.append("<p><strong>Amount paid: </strong>₹").append(payment.getAmount()).append("</p></br>")
                    .append("<p><strong>Payment ID:</strong> ").append(payment.getRazorpayPaymentId()).append("</p>")
                    .append("</br>").append("</br>");
        }
        if(Objects.nonNull(booking.getSpecialRequest())){
            content.append("<p><strong>Special request: </strong> ").append(booking.getSpecialRequest()).append("</p>").append("</br>");
        }
        content.append("<p><strong>Status: </strong> ").append(booking.getStatus()).append("</p>").append("</br>").append("</br>")
                .append("<p><strong>\uD83D\uDDD3\uFE0F Check-in:</strong> ").append(booking.getCheckInDate().format(dateFormatter)).append(" 12:00 PM").append("</p>").append("</br>")
                .append("<p><strong>\uD83D\uDDD3\uFE0F Check-out:</strong> ").append(booking.getCheckOutDate().format(dateFormatter)).append(" 11:00 AM").append("</p>").append("</br>");
        if(Objects.nonNull(roomNumbers)) {
            content.append("<p><strong>Room(s):</strong> ").append(roomNumbers).append("</p>").append("</br>");
        }
        content.append("<p><strong>No. of adults:</strong> ").append(booking.getNoOfAdults()).append("</p>").append("</br>")
                .append("<p><strong>No. of childrens:</strong> ").append(booking.getNoOfChildrens()).append("</p>").append("</br>")
                .append("<p>If you have any special requests or need assistance before your arrival, feel free to reply to this email or call us directly.</p>")
                .append("</br>").append("</br>")
                .append("<p>We can’t wait to welcome you</p>").append("</br>")
                .append("<p>Warm regards,</p>").append("</br>")
                .append("<p>Team Hotel Pride</p>").append("</br>")
                .append("<p>\uD83D\uDCDE 098199 14047</p>").append("</br>")
                .append("\uD83C\uDF10 <a href='http://hotelpride.com'>hotelpride.com</a>");

        // Send email
        this.sendPaymentConfirmation(email, subject, content.toString());
    }

    public void sendPaymentConfirmation(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String tempEmail = toEmail;
            helper.setTo(tempEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
//            helper.addAttachment("Invoice.pdf", new ByteArrayResource(invoicePdf));

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    public void sendBookingRequestEmail(String name, String email,  String content){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String tempEmail = email;
            helper.setTo(tempEmail);
            helper.setSubject("Booking request from "+name);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    @Async
    public void sendBookingQueryMailToHotelOwner(String email, BookingRequestQueryDto request) {
        // Construct the email body
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(new Date());
        String content = "<h2>Booking request raised by  " + request.getFullName() + ",</h2>" +
                "<p>Here are the booking details</p>" +
                "<p><strong>Customer name:</strong> " + request.getFullName() + "</p>" +
                "<p><strong>Customer email:</strong> " + request.getEmail() + "</p>" +
                "<p><strong>Customer phone:</strong> " + request.getPhoneNumber() + "</p>" +
                "<p><strong>No of guests:</strong> " + request.getNoOfGuests() + "</p>" +
                "<p><strong>No of rooms:</strong> " + request.getNoOfRooms() + "</p>" +
                "<p><strong>Request date:</strong> " + formattedDate + "</p>" +
                "<br>";
        this.sendBookingRequestEmail(request.getFullName(), email, content);
    }

    @Async
    public void sendPasswordResetOtp(String email, String otp) {
        String subject = "Password reset OTP";
        String content = "<p><strong>Password reset OTP for </strong> " + email + " is " + otp + "</p>";
        this.sendEmail(email, subject, content);
    }

    public void sendEmail(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //            String tempEmail = "waqarmohd99@gmail.com";
            String tempEmail = toEmail;
            helper.setTo(tempEmail);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

}
