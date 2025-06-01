package com.priyhotel.exception;

public class UserAlreadyRegisteredException extends RuntimeException{

    public UserAlreadyRegisteredException(String message){
        super(message);
    }
}
