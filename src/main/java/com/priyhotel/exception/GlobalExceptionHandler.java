package com.priyhotel.exception;

import com.priyhotel.dto.DefaultErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class)))
    public ResponseEntity<DefaultErrorResponse> handleResourceNotFound(ResourceNotFoundException ex){
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        DefaultErrorResponse response = DefaultErrorResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class)))
    public ResponseEntity<DefaultErrorResponse> handleBadRequest(BadRequestException ex){
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        DefaultErrorResponse response = DefaultErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

//    @ExceptionHandler(InternalServerError.class)
//    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class)))
//    public ResponseEntity<DefaultErrorResponse> handleInternalServerError(InternalServerError ex){
//        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
//        DefaultErrorResponse response = DefaultErrorResponse.builder()
//                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .message(ex.getMessage())
//                .build();
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    @ApiResponse(responseCode = "409", description = "User already registered", content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class)))
    public ResponseEntity<DefaultErrorResponse> handleUserAlreadyRegisteredError(UserAlreadyRegisteredException ex){
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        DefaultErrorResponse response = DefaultErrorResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
