package com.starnoh.sacco_management.controller;

import com.starnoh.sacco_management.dto.ApiResponse;
import com.starnoh.sacco_management.dto.ContributionResponseDto;
import com.starnoh.sacco_management.dto.RecordContributionRequest;
import com.starnoh.sacco_management.enums.ContributionStatus;
import com.starnoh.sacco_management.service.ContributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ContributionsController {

    private final ContributionService contributionService;

    @PreAuthorize("hasRole('TREASURER')")
    @PostMapping
    public ResponseEntity<ApiResponse<ContributionResponseDto>> recordContribution(
            @Valid @RequestBody RecordContributionRequest request,
            Authentication authentication
            ) {

        ContributionResponseDto response = contributionService.recordContribution(request , authentication);

        return ResponseEntity.ok(new ApiResponse<>(true, "Member contribution recorded successfully", response));

    }
}
