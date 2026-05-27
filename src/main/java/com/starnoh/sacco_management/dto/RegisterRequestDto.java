package com.starnoh.sacco_management.dto;


import lombok.Data;

@Data
public class RegisterRequestDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
}
