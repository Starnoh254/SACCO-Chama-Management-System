package com.starnoh.sacco_management.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MemberResponseDto {
    private Long id;
    private String membershipNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String nationalId;
    private LocalDate dateJoined;
    private String status;
    private String address;
    private Instant createdAt;

    public MemberResponseDto(Long id, String membershipNumber, String firstName, String lastName, String email, String phoneNumber, String nationalId, LocalDate dateJoined, String status, String address, Instant createdAt) {
        this.id = id;
        this.membershipNumber = membershipNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.nationalId = nationalId;
        this.dateJoined = dateJoined;
        this.status = status;
        this.address = address;
        this.createdAt = createdAt;
    }
}
