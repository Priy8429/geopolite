package com.priyhotel.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    public void sendSms(String toPhoneNumber, String message) {
//        Message.creator(
//                new com.twilio.type.PhoneNumber(toPhoneNumber),
//                new com.twilio.type.PhoneNumber(fromPhoneNumber),
//                message
//        ).create();
    }
}
