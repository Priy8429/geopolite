package com.priyhotel.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class OffersUpdateDto {

    private Long hotelId;
    private LocalDate offerStartDate;
    private LocalDate offerEndDate;
    private Map<String, Double> offerRoomMap;
}
