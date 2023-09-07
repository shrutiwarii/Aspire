package com.aspire.aspireproject.exception;

public class NoAuthTokenException extends RuntimeException{
    public NoAuthTokenException(String message){
        super(message);
    }
}
