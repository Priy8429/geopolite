package com.priyhotel.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

    private Long id;
    private String email;

    private String contactNumber;

    private String name;

    private String token;
}
