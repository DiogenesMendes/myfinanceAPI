package com.diogeMendes.personalFinance.exception;

public class AuthenticationException extends RuntimeException{
    public AuthenticationException (String message){
        super(message);
    }
}
