package com.priyhotel.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReviewRequestDto {

    private Long hotelId;
    private Long userId;
    private String comment;
    private int rating;
    private List<Long> assetIds;
}
