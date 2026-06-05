package com.starnoh.sacco_management.dto;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateMemberRequest {

    // User table fields
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;

    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    // Members table fields
    @Size(max = 50, message = "National ID must not exceed 50 characters")
    private String nationalId;

    private String address;

    @PastOrPresent(message = "Date joined must not be a future date")
    private LocalDate dateJoined;

    @Pattern(regexp = "^(ACTIVE|INACTIVE)$",
            message = "Status must be ACTIVE or INACTIVE")
    private String status;
}
