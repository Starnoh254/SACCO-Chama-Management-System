package com.starnoh.sacco_management.dto;

import com.starnoh.sacco_management.entity.Users;
import lombok.Data;

import java.time.Instant;

@Data
public class UserMembershipApplicationResponseDto {

    private Long id;
    private String nationalId;
    private String address;
    private String applicationStatus;
    private Instant appliedAt;
    private Users reviewedBy;
    private Instant reviewedAt;

    public UserMembershipApplicationResponseDto(Long id, String nationalId, String address, String applicationStatus, Instant appliedAt, Users reviewedBy, Instant reviewedAt) {
        this.id = id;
        this.nationalId = nationalId;
        this.address = address;
        this.applicationStatus = applicationStatus;
        this.appliedAt = appliedAt;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
    }
}
