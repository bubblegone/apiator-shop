package com.apiator.shop.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<Map<String, String>> handleException(ApiException e) {
        return new ResponseEntity<>(e.getResponse(), e.getHttpStatus());
    }
}
