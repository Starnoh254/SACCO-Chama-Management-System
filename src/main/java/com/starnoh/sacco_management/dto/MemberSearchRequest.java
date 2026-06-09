package com.starnoh.sacco_management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberSearchRequest {

    // Full-text keyword (searches across multiple fields)
    private String keyword;
    // Individual field filters
    private String firstName;
    private String lastName;
    private String membershipNumber;
    private String nationalId; // Admin-only; ignored for other roles
    private String status; // ACTIVE | INACTIVE
    private String address; // Admin-only; ignored for other roles
    // Date range filters
    private LocalDate dateJoinedFrom;
    private LocalDate dateJoinedTo;
    // Pagination & sorting
    @Min(value = 0, message = "Page must be 0 or greater")
    private int page = 0;
    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size must not exceed 100")
    private int size = 20;

    @Pattern(regexp =
            "^(firstName|lastName|dateJoined|createdAt|membershipNumber)$",
            message = "Invalid sortBy field")
    private String sortBy = "createdAt";
    @Pattern(regexp = "^(ASC|DESC)$", message = "Direction must be ASC or DESC")
    private String direction = "DESC";
}
