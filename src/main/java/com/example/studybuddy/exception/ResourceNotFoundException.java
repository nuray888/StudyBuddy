package com.example.studybuddy.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {

      super(message);
    }
}
