package com.example.watchtogether.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String message;
    private final String type;
}
