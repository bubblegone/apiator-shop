package com.apiator.shop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InternalExceptionHandler {
    @ExceptionHandler(value = InternalException.class)
    public ResponseEntity<String> handleException(ApiException e) {
        return new ResponseEntity<>("Internal exception", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
