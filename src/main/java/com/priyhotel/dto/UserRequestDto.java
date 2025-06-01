package com.priyhotel.dto;

import com.priyhotel.constants.Role;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestDto {

    private String email;

    private String contactNumber;

    private String name;

    private String password;

    private Role role;
}
