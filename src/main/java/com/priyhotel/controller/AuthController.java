package com.priyhotel.controller;

import com.priyhotel.dto.*;
import com.priyhotel.entity.User;
import com.priyhotel.mapper.UserMapper;
import com.priyhotel.service.AuthService;
import com.priyhotel.service.EmailService;
import com.priyhotel.service.OtpService;
import com.priyhotel.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    OtpService otpService;

    @Autowired
    SmsService smsService;

    @Autowired
    EmailService emailService;

    @Autowired
    UserMapper userMapper;

    @PostMapping("/reset-password/request-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String phoneOrEmail) {

        try{
            authService.sendPasswordResetOtp(phoneOrEmail);
            return ResponseEntity.ok("OTP sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    DefaultErrorResponse.builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error sending sms, please try later")
                            .build());
        }

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

    @PostMapping("reset-password/verify-otp")
    public ResponseEntity<?> verifyPasswordResetOtp(@RequestBody PasswordResetDto dto){
        try{
            boolean isValidOtp = authService.verifyPasswordResetOtp(dto.getEmail(), dto.getOtp());
            if(isValidOtp){
                return ResponseEntity.ok("OTP Verified successfully!");
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        DefaultErrorResponse.builder()
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .message("Invalid OTP!")
                                .build());
            }
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    DefaultErrorResponse.builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error sending sms, please try later")
                            .build());
        }
    }

    @PostMapping("reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDto dto){
        try{
            authService.verifyOtpAndSaveNewPassword(dto.getEmail(), dto.getOtp(), dto.getNewPassword());
            return ResponseEntity.ok("Password updated successfully!");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    DefaultErrorResponse.builder()
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build());
        }catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    DefaultErrorResponse.builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error sending sms, please try later")
                            .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequestDto user){
        User registeredUser = authService.register(user);
        return ResponseEntity.ok(userMapper.toDto(registeredUser));
    }

//    @PutMapping("/user")
//    public ResponseEntity<?> updateUser(@RequestBody User user){
//        try {
//            authService.updateUser(user);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
//        }
//        return ResponseEntity.ok(user);
//    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest.getEmail(), loginRequest.getPassword()));
    }
}
