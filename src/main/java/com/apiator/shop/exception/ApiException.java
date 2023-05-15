package com.apiator.shop.exception;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class ApiException extends RuntimeException{
    private final HttpStatus httpStatus;

    public ApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public Map<String, String> getResponse(){
        Map<String, String> map = new HashMap<>();
        map.put("message", getMessage());
        return map;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
