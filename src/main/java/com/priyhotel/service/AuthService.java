package com.priyhotel.service;

import com.priyhotel.constants.Constants;
import com.priyhotel.dto.UserDto;
import com.priyhotel.dto.UserRequestDto;
import com.priyhotel.dto.UserResponseDto;
import com.priyhotel.entity.User;
import com.priyhotel.exception.BadRequestException;
import com.priyhotel.exception.ResourceNotFoundException;
import com.priyhotel.mapper.UserMapper;
import com.priyhotel.repository.UserRepository;
import com.priyhotel.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    public String rawRegister(User user){
        userRepository.save(user);
        return Constants.USER_CREATED_SUCCESS_MSG;
    }

    public UserDto register(UserRequestDto user){
//        Optional<User> existingUser = userRepository.findByContactNumber(user.getContactNumber());
//        if(existingUser.isPresent()){
//            user.setPassword(passwordEncoder.encode(user.getPassword()));
//            user.setId(existingUser.get().getId());
//            userRepository.save(user);
//            return Constants.USER_CREATED_SUCCESS_MSG;
//        }else{
//            throw new RuntimeException("User not found!");
//        }
        Optional<User> userByEmail = userRepository.findByEmail(user.getEmail());
        Optional<User> userByContactNumber = userRepository.findByContactNumber(user.getContactNumber());
        if(userByContactNumber.isPresent() || userByEmail.isPresent()){
            throw new BadRequestException("User email/phone already registered!");
        }

        User newUser = new User();
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        newUser.setContactNumber(user.getContactNumber());
        newUser.setRole(user.getRole());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(newUser);
        return userMapper.toDto(savedUser);
    }

//    public UserDto updateUser(User user){
//        User existingUser = this.getUserByEmailOrPhone(user.getEmail(), user.getContactNumber())
//                .orElseThrow(() ->new BadRequestException("User not found with the given email or phone"));
//        user.setId(existingUser.getId());
//        User savedUser = userRepository.save(user);
//        return userMapper.toDto(savedUser);
//    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found!"));
    }

    public User getUserByPhone(String phoneNumber){
        return userRepository.findByContactNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User with this contact number does not exist"));
    }

    public Optional<User> getUserByEmailOrPhone(String email, String phoneNumber){
        return userRepository.findByEmailOrContactNumber(email, phoneNumber);
    }

    public UserResponseDto login(String emailOrPhone, String password) {
        User user = this.getUserByEmailOrPhone(emailOrPhone, emailOrPhone)
                .orElseThrow(() -> new BadRequestException("User not found with the given email or phone"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Invalid phone or password");
        }
        String token = jwtUtil.generateToken(user);
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .contactNumber(user.getContactNumber())
                .token(token)
                .build();
    }

}
