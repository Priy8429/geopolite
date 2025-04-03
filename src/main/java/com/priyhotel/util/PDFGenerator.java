package com.priyhotel.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.priyhotel.entity.Booking;
import com.priyhotel.entity.Payment;
import com.priyhotel.entity.RoomBooking;
import com.priyhotel.entity.User;
import com.priyhotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.Optional;

@Service
public class PDFGenerator {

    @Autowired
    UserRepository userRepository;

    public byte[] generateInvoice(Booking booking, Payment payment) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Add Hotel Logo
            Image logo = Image.getInstance("src/main/resources/static/logo.png"); // Update path
            logo.scaleAbsolute(100, 50);
            logo.setAlignment(Element.ALIGN_LEFT);
            document.add(logo);

            // Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Hotel Booking Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Booking Details Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);
            table.setWidths(new float[]{3, 7});

            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

            User user = booking.getUser();

            addTableCell(table, "Booking ID:", boldFont);
            addTableCell(table, String.valueOf(booking.getBookingNumber()), normalFont);
            addTableCell(table, "Customer Name:", boldFont);
            addTableCell(table, user.getName(), normalFont);
            addTableCell(table, "Email:", boldFont);
            addTableCell(table, user.getEmail(), normalFont);
//            addTableCell(table, "Room Type:", boldFont);
//            addTableCell(table, String.valueOf(payment.getBooking().getRoom().getRoomType()), normalFont);
            addTableCell(table, "Check-in Date:", boldFont);
            addTableCell(table, booking.getCheckInDate().toString(), normalFont);
            addTableCell(table, "Check-out Date:", boldFont);
            addTableCell(table, booking.getCheckOutDate().toString(), normalFont);
            addTableCell(table, "Total Amount:", boldFont);
            addTableCell(table, "₹ " + booking.getTotalAmount(), normalFont);
            addTableCell(table, "Payment Type:", boldFont);
            addTableCell(table, booking.getPaymentType().toString(), normalFont);

            document.add(table);

            if(Objects.nonNull(payment)){
                Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
                Paragraph paymentTitle = new Paragraph("Payment details", subTitleFont);
                document.add(paymentTitle);

                PdfPTable paymentTable = new PdfPTable(2);
                paymentTable.addCell("Payment status: ");
                paymentTable.addCell(payment.getStatus());
                paymentTable.addCell("Payment amount: ");
                paymentTable.addCell(String.valueOf(booking.getTotalAmount()));
                paymentTable.addCell("Discount amount: ");
                paymentTable.addCell(String.valueOf(booking.getTotalAmount()-payment.getAmount()));
                paymentTable.addCell("Total amount: ");
                paymentTable.addCell(String.valueOf(payment.getAmount()));
                document.add(paymentTable);
            }


            // QR Code for Payment ID
//            Image qrCode = generateQRCode(payment.getRazorpayPaymentId());
//            qrCode.setAlignment(Element.ALIGN_CENTER);
//            qrCode.scaleAbsolute(100, 100);
//            document.add(new Paragraph("Scan to Verify Payment", new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC)));
//            document.add(qrCode);

            // Add Booked Rooms
            PdfPTable roomTable = new PdfPTable(5);
            table.addCell("Room Number");
            table.addCell("Room Type");
            table.addCell("No. of Adults");
            table.addCell("No. of Childrens");
            table.addCell("No. of Nights");

            for (RoomBooking room : booking.getBookedRooms()) {
                table.addCell(room.getRoom().getRoomNumber());
                table.addCell(room.getRoom().getRoomType().getTypeName());
                table.addCell(String.valueOf(room.getNoOfAdults()));
                table.addCell(String.valueOf(room.getNoOfChilds()));
                table.addCell(String.valueOf(room.getNoOfNights()));
            }

            document.add(roomTable);

            // Footer
            document.add(new Paragraph("\n\nThank you for choosing Hotel Pride!", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            document.add(new Paragraph("For any inquiries, contact us at support@hotel.com", new Font(Font.FontFamily.HELVETICA, 10)));

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF invoice: " + e.getMessage(), e);
        }
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private Image generateQRCode(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 100, 100);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int y = 0; y < bitMatrix.getHeight(); y++) {
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                out.write(bitMatrix.get(x, y) ? 0 : 255);
            }
        }
        Image qrImage = Image.getInstance(out.toByteArray());
        return qrImage;
    }
}
