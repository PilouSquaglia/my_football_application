package com.example.statsservice.exception;

public class StatNotFoundException extends RuntimeException {

    public StatNotFoundException(String message) {
        super(message);
    }
}
