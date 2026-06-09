package com.starnoh.sacco_management.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class MemberSearchResponseDto {

    private Long id;
    private String membershipNumber;
    private String firstName;
    private String lastName;
    private String status;
    // Hidden from MEMBER role
    private String email;
    private String phoneNumber;
    private LocalDate dateJoined;
    private Instant createdAt;
    // Hidden from MEMBER and TREASURER roles
    private String nationalId;
    // Hidden from MEMBER role
    private String address;

}
