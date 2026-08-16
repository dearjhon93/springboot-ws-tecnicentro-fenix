package com.imjhon.wsfenix.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String message;
    private String username;

    public LoginResponse(String message, String username) {
        this.message = message;
        this.username = username;
    }
}
