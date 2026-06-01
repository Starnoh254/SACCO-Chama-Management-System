package com.starnoh.sacco_management.dto;


import lombok.Data;

import java.time.Instant;

@Data
public class MembershipApplicationResponseDto {

    private Long id;
    private String applicationStatus;
    private Instant appliedAt;


    public MembershipApplicationResponseDto(Long id, String applicationStatus, Instant appliedAt) {
        this.id = id;
        this.applicationStatus = applicationStatus;
        this.appliedAt = appliedAt;
    }
}
