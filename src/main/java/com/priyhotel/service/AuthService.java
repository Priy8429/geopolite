package com.priyhotel.service;

import com.priyhotel.constants.Constants;
import com.priyhotel.dto.UserDto;
import com.priyhotel.dto.UserRequestDto;
import com.priyhotel.entity.User;
import com.priyhotel.exception.BadRequestException;
import com.priyhotel.exception.ResourceNotFoundException;
import com.priyhotel.mapper.UserMapper;
import com.priyhotel.repository.UserRepository;
import com.priyhotel.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        User newUser = new User();
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        newUser.setContactNumber(user.getContactNumber());
        newUser.setRole(user.getRole());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(newUser);
        return userMapper.toDto(savedUser);
    }

    public UserDto updateUser(User user){
        User existingUser = this.getUserByEmailOrPhone(user.getEmail(), user.getContactNumber());
        user.setId(existingUser.getId());
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found!"));
    }

    public User getUserByPhone(String phoneNumber){
        return userRepository.findByContactNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User with this contact number does not exist"));
    }

    public User getUserByEmailOrPhone(String email, String phoneNumber){
        return userRepository.findByEmailOrContactNumber(email, phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the given email or phone"));
    }

    public String login(String emailOrPhone, String password){
        User user  = this.getUserByEmailOrPhone(emailOrPhone, emailOrPhone);
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BadRequestException("Invalid phone or password");
        }
        return jwtUtil.generateToken(user);
    }
}
