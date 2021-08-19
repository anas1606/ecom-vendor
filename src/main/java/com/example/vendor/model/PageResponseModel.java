package com.example.vendor.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class PageResponseModel {
    private String message;
    private HttpStatus status;
    private int statusCode;
    private Object data;
    private Object result;
}