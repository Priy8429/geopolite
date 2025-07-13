package com.priyhotel.service;

import com.priyhotel.constants.PaymentStatus;
import com.priyhotel.entity.Payment;
import com.priyhotel.repository.RoomBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RoomBookingService {

    @Autowired
    RoomBookingRepository roomBookingRepository;

}
