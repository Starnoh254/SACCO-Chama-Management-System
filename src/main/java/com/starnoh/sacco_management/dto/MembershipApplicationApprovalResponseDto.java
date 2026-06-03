package com.starnoh.sacco_management.dto;

import lombok.Data;

import java.time.Instant;


@Data
public class MembershipApplicationApprovalResponseDto {

    private Long applicationId;
    private String applicationStatus;
    private Long reviewedBy;
    private Instant reviewedAt;

    public MembershipApplicationApprovalResponseDto(Long applicationId, String applicationStatus, Long reviewedBy, Instant reviewedAt) {
        this.applicationId = applicationId;
        this.applicationStatus = applicationStatus;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
    }


}
