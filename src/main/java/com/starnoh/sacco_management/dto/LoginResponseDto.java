package com.starnoh.sacco_management.dto;

import lombok.Data;

@Data
public class LoginResponseDto {

    private TokenResponseDto tokenResponseDto;
    private UserResponseDto user;

    public LoginResponseDto(TokenResponseDto tokenResponseDto, UserResponseDto user) {
        this.tokenResponseDto = tokenResponseDto;
        this.user = user;
    }
}
