package com.priyhotel.dto;

import java.time.Instant;

public class OtpEntry {
    private final String otp;
    private final Instant expiryTime;

    public OtpEntry(String otp, Instant expiryTime) {
        this.otp = otp;
        this.expiryTime = expiryTime;
    }

    public String getOtp() {
        return otp;
    }

    public Instant getExpiryTime() {
        return expiryTime;
    }
}
