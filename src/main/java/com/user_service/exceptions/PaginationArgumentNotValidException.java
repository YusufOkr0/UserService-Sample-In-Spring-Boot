package com.user_service.exceptions;

public class PaginationArgumentNotValidException extends RuntimeException{
    public PaginationArgumentNotValidException(String message){
        super(message);
    }
}
