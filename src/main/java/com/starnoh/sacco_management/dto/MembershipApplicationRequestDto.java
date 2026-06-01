package com.starnoh.sacco_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MembershipApplicationRequestDto {

    @NotBlank(message = "National ID is required")
    private String nationalId;

    @NotBlank(message = "Street Address is required")
    private String address;
}
