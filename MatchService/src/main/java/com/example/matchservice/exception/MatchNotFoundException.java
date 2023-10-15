package com.example.matchservice.exception;

public class MatchNotFoundException extends RuntimeException {

    public MatchNotFoundException(String message) {
        super(message);
    }
}
