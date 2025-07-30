package com.priyhotel.service;

import com.priyhotel.dto.OtpEntry;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final int OTP_VALID_DURATION = 10 * 60; // 5 minutes (in seconds)

    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generateOtp(String phoneOrEmail) {
        removeExpiredOtps(); // Cleanup before generating a new OTP

        String otp = String.format("%04d", random.nextInt(10000)); // 4-digit OTP
        Instant expiryTime = Instant.now().plusSeconds(OTP_VALID_DURATION);

        otpStorage.put(phoneOrEmail, new OtpEntry(otp, expiryTime));
        return otp;
    }

    public boolean validateOtp(String phoneNumber, String enteredOtp) {
        OtpEntry otpEntry = otpStorage.get(phoneNumber);

        if (Instant.now().isAfter(otpEntry.getExpiryTime())) {
            otpStorage.remove(phoneNumber); // Expired OTP, remove it
            return false;
        }

        removeExpiredOtps(); // Cleanup before validating

        if (otpEntry == null) {
            return false; // No OTP found
        }

        if (otpEntry.getOtp().equals(enteredOtp)) {
            otpStorage.remove(phoneNumber); // OTP is valid, remove it after use
            return true;
        }

        return false; // OTP is incorrect
    }

    public void clearOtp(String phoneNumber) {
        otpStorage.remove(phoneNumber);
    }

    /**
     * Removes expired OTPs from the storage
     */
    private void removeExpiredOtps() {
        Instant now = Instant.now();
        Iterator<Map.Entry<String, OtpEntry>> iterator = otpStorage.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, OtpEntry> entry = iterator.next();
            if (now.isAfter(entry.getValue().getExpiryTime())) {
                iterator.remove(); // Remove expired OTP
            }
        }
    }

}
