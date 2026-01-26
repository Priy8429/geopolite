package com.priyhotel.dto;

import com.priyhotel.entity.Amenity;
import com.priyhotel.entity.Asset;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeAvailabilityResponse {

    List<RoomTypeAvailabilityDto> roomTypeAvailabilities;
    List<BookingResponseDto> eventBookings;
}
