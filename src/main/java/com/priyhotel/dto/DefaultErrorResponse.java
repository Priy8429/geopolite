package com.priyhotel.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DefaultErrorResponse {

    private int statusCode;
    private String message;
}
