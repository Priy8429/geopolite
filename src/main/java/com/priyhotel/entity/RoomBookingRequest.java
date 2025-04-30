package com.priyhotel.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "room_booking_request")
public class RoomBookingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomTypeId;

    private Integer noOfRooms;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}
