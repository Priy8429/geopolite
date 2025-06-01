package com.priyhotel.mapper;

import com.priyhotel.dto.RoomRequestDto;
import com.priyhotel.dto.UserDto;
import com.priyhotel.entity.Room;
import com.priyhotel.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user){
        return UserDto.builder()
            .name(user.getName())
            .email(user.getEmail())
            .contactNumber(user.getContactNumber())
                .build();
    }
}
