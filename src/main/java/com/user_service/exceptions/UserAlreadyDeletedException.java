package com.user_service.exceptions;

public class UserAlreadyDeletedException extends RuntimeException{
    public UserAlreadyDeletedException(String message   ) {
        super(message);
    }
}
