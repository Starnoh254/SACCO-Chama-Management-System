package com.starnoh.sacco_management.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class ContributionResponseDto {

    private Long id;
    private Long memberId;
    private String membershipNumber;
    private String memberName;
    private BigDecimal amount;
    private LocalDate contributionDate;
    private String paymentMethod;
    private String referenceNumber;
    private String status;
    private Instant createdAt;
}
