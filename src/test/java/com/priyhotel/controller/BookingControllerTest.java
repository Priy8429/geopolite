package com.priyhotel.controller;


import com.priyhotel.dto.BookingRequestDto;
import com.priyhotel.dto.BookingResponseDto;
import com.priyhotel.entity.Booking;
import com.priyhotel.mapper.BookingMapper;
import com.priyhotel.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @InjectMocks
    private BookingController bookingController;

    @Mock
    private BookingService bookingService;

    @Mock
    private BookingMapper bookingMapper;

    private BookingRequestDto bookingRequestDto;
    private Booking booking;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void setUp(){
        bookingRequestDto = new BookingRequestDto();
        booking = new Booking();
        bookingResponseDto = new BookingResponseDto();
    }

    @Test
    void testCreateBooking_success(){
        Mockito.when(bookingService.createBooking(bookingRequestDto)).thenReturn(booking);
        Mockito.when(bookingMapper.toResponseDto(booking)).thenReturn(bookingResponseDto);

        ResponseEntity<?> response = bookingController.createBooking(bookingRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(bookingResponseDto, response.getBody());
    }
}


