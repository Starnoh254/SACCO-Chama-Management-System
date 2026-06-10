package com.starnoh.sacco_management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecordContributionRequest {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01" , message = "Amount must be greater than zero")
    @Digits(integer = 13, fraction = 2, message = "Amount must have at most 2 decimal places")
    private BigDecimal amount;

    @PastOrPresent(message = "Contribution date cannot be in the future")
    private LocalDate contributionDate; // defaults to today if null

    @NotBlank(message = "Payment method is required")
    @Pattern(
            regexp = "^(Cash|Mobile Money|Bank Transfer)$",
            message = "Payment method must be : Cash, Mobile Money , or Bank Transfer"
    )
    private String paymentMethod;

    @Size(max = 100 , message = "Reference number must not exceed 100 characters")
    private String referenceNumber; // optional

    @Pattern(
            regexp = "^(PAID|PENDING)$",
            message = "Status must be PAID or PENDING"
    )
    private String status; // defaults to PAID if null
}
