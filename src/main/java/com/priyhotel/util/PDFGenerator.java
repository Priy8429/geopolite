package com.priyhotel.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.priyhotel.entity.Payment;
import com.priyhotel.entity.User;
import com.priyhotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

@Service
public class PDFGenerator {

    @Autowired
    UserRepository userRepository;

    public byte[] generateInvoice(Payment payment) {
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

            User user = payment.getBooking().getUser();

            addTableCell(table, "Booking ID:", boldFont);
            addTableCell(table, String.valueOf(payment.getBooking().getId()), normalFont);
            addTableCell(table, "Customer Name:", boldFont);
            addTableCell(table, user.getName(), normalFont);
            addTableCell(table, "Email:", boldFont);
            addTableCell(table, user.getEmail(), normalFont);
            addTableCell(table, "Room Type:", boldFont);
            addTableCell(table, String.valueOf(payment.getBooking().getRoom().getRoomType()), normalFont);
            addTableCell(table, "Check-in Date:", boldFont);
            addTableCell(table, payment.getBooking().getCheckInDate().toString(), normalFont);
            addTableCell(table, "Check-out Date:", boldFont);
            addTableCell(table, payment.getBooking().getCheckOutDate().toString(), normalFont);
            addTableCell(table, "Total Amount:", boldFont);
            addTableCell(table, "₹ " + payment.getAmount(), normalFont);
            addTableCell(table, "Payment Status:", boldFont);
            addTableCell(table, payment.getStatus(), normalFont);

            document.add(table);

            // QR Code for Payment ID
            Image qrCode = generateQRCode(payment.getRazorpayPaymentId());
            qrCode.setAlignment(Element.ALIGN_CENTER);
            qrCode.scaleAbsolute(100, 100);
            document.add(new Paragraph("Scan to Verify Payment", new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC)));
            document.add(qrCode);

            // Footer
            document.add(new Paragraph("\n\nThank you for choosing our hotel!", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
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
