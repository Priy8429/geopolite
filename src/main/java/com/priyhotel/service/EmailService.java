package com.priyhotel.service;

import com.priyhotel.dto.BookingRequestQueryDto;
import com.priyhotel.entity.Booking;
import com.priyhotel.entity.Payment;
import com.priyhotel.entity.User;
import com.priyhotel.util.PDFGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    PDFGenerator pdfGenerator;

    @Async
    public void sendPaymentConfirmationEmailToUser(Booking booking, Payment payment) {

        // Generate PDF Invoice
        byte[] pdfInvoice = pdfGenerator.generateInvoice(booking, payment);

        User user = booking.getUser();
        String email = user.getEmail();
        String subject = "Booking Confirmation - Booking #" + booking.getBookingNumber();

        StringBuilder content =  new StringBuilder();

        content.append("<h2>Dear ").append(user.getName()).append(",</h2>");
        if(Objects.nonNull(payment)){
            content.append("<p>Your payment of <strong>₹").append(payment.getAmount()).append("</strong> has been successfully received.</p>")
                    .append("<p><strong>Payment ID:</strong> ").append(payment.getRazorpayPaymentId()).append("</p>");
        }

                content.append("<p><strong>Booking ID:</strong> ").append(booking.getBookingNumber()).append("</p>")
                .append("<p><strong>Payment mode:</strong> ").append(booking.getPaymentType()).append("</p>")
                .append("<p><strong>Status:</strong> ").append(booking.getStatus()).append("</p>")
                .append("<p>Thank you for choosing Hotel Pride!</p>")
                .append("<br>")
                .append("<p>Best Regards,</p>")
                .append("<p>Hotel Pride</p>");

        this.sendPaymentConfirmation(email, subject, content.toString(), pdfInvoice);
    }

    @Async
    public void sendPaymentConfirmationEmailToOwner(Booking booking, Payment payment) {

        // Generate PDF Invoice
        byte[] pdfInvoice = pdfGenerator.generateInvoice(booking, payment);

        String email = booking.getHotel().getEmail();
        String subject = "Booking Received - Booking #" + booking.getBookingNumber();

//        // Start building the HTML table
//        StringBuilder tableHtml = new StringBuilder();
//        tableHtml.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse; width: 100%;'>");
//        tableHtml.append("<tr>")
//                .append("<th>Room Number</th>")
//                .append("<th>Room Type</th>")
//                .append("<th>No of Adults</th>")
//                .append("<th>No of Children</th>")
//                .append("<th>No of Nights</th>")
//                .append("</tr>");
//
//        for (RoomBooking room : payment.getBooking().getBookedRooms()) {
//            tableHtml.append("<tr>")
//                    .append("<td>").append(room.getRoom().getRoomNumber()).append("</td>")
//                    .append("<td>").append(room.getRoom().getRoomType().getTypeName()).append("</td>")
//                    .append("<td>").append(room.getNoOfAdults()).append("</td>")
//                    .append("<td>").append(room.getNoOfChilds()).append("</td>")
//                    .append("<td>").append(room.getNoOfNights()).append("</td>")
//                    .append("</tr>");
//        }
//
//        tableHtml.append("</table>");

        // Construct the email body
        StringBuilder content =  new StringBuilder();
        content.append("<h2>Dear ").append(booking.getHotel().getEmail()).append(",</h2>");
        // add payment information if prepaid
        if(Objects.nonNull(payment)){
            content.append("<p>Payment of <strong>₹").append(payment.getAmount()).append("</strong> has been successfully received.</p>").
                    append("<p><strong>Payment ID:</strong> ").append(payment.getRazorpayPaymentId()).append("</p>");
        }
        content.append("<p><strong>Booking ID:</strong> ").append(booking.getBookingNumber()).append("</p>")
                .append("<p><strong>Payment Type:</strong> ").append(booking.getPaymentType()).append("</p>")
                .append("<p><strong>Status:</strong> ").append(booking.getStatus()).append("</p>")
                .append("<p><strong>Rooms Booked:</strong> ").append(booking.getTotalRooms()).append("</p>")
//                + "<h3>Room Details:</h3>"
//                + tableHtml.toString()  // Add the table here
                .append("<br>")
                .append("<p>Thank you for choosing Hotel Pride!</p>")
                .append("<br>")
                .append("<p>Best Regards,</p>")
                .append("<p>Hotel Pride</p>");

        // Send email
        this.sendPaymentConfirmation(email, subject, content.toString(), pdfInvoice);
    }

    public void sendPaymentConfirmation(String toEmail, String subject, String content, byte[] invoicePdf) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //            String tempEmail = "waqarmohd99@gmail.com";
            String tempEmail = "princevishwakarma510@gmail.com";
            helper.setTo(tempEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.addAttachment("Invoice.pdf", new ByteArrayResource(invoicePdf));

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    public void sendBookingRequestEmail(String name, String email,  String content){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            String tempEmail = "waqarmohd99@gmail.com";
            String tempEmail = "princevishwakarma510@gmail.com";
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
}
