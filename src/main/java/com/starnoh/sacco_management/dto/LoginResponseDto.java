package com.starnoh.sacco_management.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
    private String accessToken;
    private String tokenType;
    private UserResponseDto user;


    public LoginResponseDto(String accessToken, String tokenType, UserResponseDto user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.user = user;
    }
}
