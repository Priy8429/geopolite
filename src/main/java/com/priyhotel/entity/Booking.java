package com.priyhotel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.priyhotel.constants.BookingSource;
import com.priyhotel.constants.BookingStatus;
import com.priyhotel.constants.PaymentType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Fetch;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="bookings")
public class Booking extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//
//    @ManyToMany
//    @JoinTable(
//            name = "booking_rooms",
//            joinColumns = @JoinColumn(name = "booking_id"),
//            inverseJoinColumns = @JoinColumn(name = "room_id")
//    )
//    private List<Room> rooms;

    private String bookingNumber;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate checkInDate;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private Double totalAmount;

    private Boolean couponApplied;

    private String couponCode;

    private Double payableAmount;

    private Integer totalRooms;

    private Integer noOfAdults;

    private Integer noOfChildrens;

    @Enumerated(EnumType.STRING)
    private BookingSource bookingSource;

    @Column(nullable = true)
    private String specialRequest;

    @JsonIgnore
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RoomBooking> bookedRooms;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType; // PREPAID, POSTPAID, PARTIALLY_PAID

    @Enumerated(EnumType.STRING)
    private BookingStatus status; // CONFIRMED, CANCELLED, PENDING
}
