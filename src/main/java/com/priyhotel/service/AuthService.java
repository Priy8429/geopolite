package com.priyhotel.service;

import com.priyhotel.constants.Constants;
import com.priyhotel.entity.User;
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

    public String rawRegister(User user){
        userRepository.save(user);
        return Constants.USER_CREATED_SUCCESS_MSG;
    }

    public String register(User user){
        Optional<User> existingUser = userRepository.findByContactNumber(user.getContactNumber());
        if(existingUser.isPresent()){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setId(existingUser.get().getId());
            userRepository.save(user);
            return Constants.USER_CREATED_SUCCESS_MSG;
        }else{
            throw new RuntimeException("User not found!");
        }


    }

    public User updateUser(User user){
        Optional<User> existingUser = userRepository.findByContactNumber(user.getContactNumber());
        if(existingUser.isPresent()){
            return userRepository.save(user);
        }else{
            throw new RuntimeException("User not found!");
        }
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found!"));
    }

    public String login(String email, String password){
        User user  = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BadCredentialsException("Invalid password");
        }
        return jwtUtil.generateToken(user);
    }
}
