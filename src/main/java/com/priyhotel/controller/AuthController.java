package com.priyhotel.controller;

import com.priyhotel.dto.LoginRequest;
import com.priyhotel.entity.User;
import com.priyhotel.service.AuthService;
import com.priyhotel.service.OtpService;
import com.priyhotel.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    OtpService otpService;

    @Autowired
    SmsService smsService;

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String phoneNumber) {
        String otp = otpService.generateOtp(phoneNumber);
        smsService.sendSms(phoneNumber, "Your OTP for registration is: " + otp);
        log.info("OTP sent to phone number: {}", otp);
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String phoneNumber, @RequestParam String otp) {
        if (otpService.validateOtp(phoneNumber, otp)) {
            User newUser = new User();
            newUser.setContactNumber(phoneNumber);
            authService.rawRegister(newUser);
            return ResponseEntity.ok("OTP verified successfully");
        }
        return ResponseEntity.status(400).body("Invalid OTP");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user){
        return ResponseEntity.ok(authService.register(user));
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody User user){
        try {
            authService.updateUser(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest.getEmail(), loginRequest.getPassword()));
    }
}
