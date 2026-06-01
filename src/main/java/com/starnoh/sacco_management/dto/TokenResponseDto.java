package com.starnoh.sacco_management.dto;

import lombok.Data;

@Data
public class TokenResponseDto {
    private String accessToken;
    private String tokenType;
    private String refreshToken;

    public TokenResponseDto(String accessToken, String tokenType, String refreshToken) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.refreshToken = refreshToken;
    }

}
