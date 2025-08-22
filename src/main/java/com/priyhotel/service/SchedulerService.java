package com.priyhotel.service;


import com.priyhotel.constants.PaymentStatus;
import com.priyhotel.entity.Payment;
import com.priyhotel.repository.PaymentRepository;
import com.priyhotel.repository.RoomBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class SchedulerService {

    @Autowired
    TaskScheduler taskScheduler;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    BookingService bookingService;

    // Track scheduled tasks to cancel them if payment succeeds early
    private final Map<Long, ScheduledFuture<?>> pendingTimeouts = new ConcurrentHashMap<>();


    // Call this when payment is initiated
    public void schedulePaymentTimeout(Payment payment) {
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
                () -> cancelIfUnpaid(payment),
                Instant.now().plus(Duration.ofMinutes(1)) // 5-minute timeout
        );
        pendingTimeouts.put(payment.getId(), scheduledTask);
    }

    // Task to run after 5 minutes
    private void cancelIfUnpaid(Payment payment) {
        Optional<Payment> freshPayment = paymentRepository.findByRazorpayPaymentId(payment.getRazorpayPaymentId());
        if (freshPayment.isPresent() && !freshPayment.get().getStatus().equals(PaymentStatus.PAID)) {
            freshPayment.get().setStatus(PaymentStatus.FAILED);
            freshPayment.get().setUpdatedOn(LocalDate.now());
            paymentRepository.save(payment);
            bookingService.removeBookedRooms(payment.getBooking());
            pendingTimeouts.remove(payment.getId());
            System.out.println("Booked rooms freed!!!");
        }
    }

    // Call this if payment succeeds (cancel the timeout)
    public void cancelTimeout(Long paymentId) {
        ScheduledFuture<?> task = pendingTimeouts.remove(paymentId);
        if (task != null) {
            task.cancel(false); // Prevent the task from running
            System.out.println("Scheduler cancelled!!!");
        }
    }
}
