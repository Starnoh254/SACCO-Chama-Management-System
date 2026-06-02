package com.starnoh.sacco_management.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class MembershipApplicationSummaryResponseDto {
    private Long applicationId;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String nationalId;
    private String address;
    private String applicationStatus;
    private Instant appliedAt;

    public MembershipApplicationSummaryResponseDto(Long applicationId, Long userId, String firstName, String lastName, String email, String phoneNumber, String nationalId, String address, String applicationStatus, Instant appliedAt) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.nationalId = nationalId;
        this.address = address;
        this.applicationStatus = applicationStatus;
        this.appliedAt = appliedAt;
    }
}
