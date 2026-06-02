package com.starnoh.sacco_management.controller;

import com.starnoh.sacco_management.dto.*;
import com.starnoh.sacco_management.service.MembershipApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/membership-applications")
public class MembershipApplicationsController {

    private final MembershipApplicationService membershipApplicationService;

    public MembershipApplicationsController(MembershipApplicationService membershipApplicationService) {
        this.membershipApplicationService = membershipApplicationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MembershipApplicationResponseDto>> apply(
            @Valid @RequestBody MembershipApplicationRequestDto request
    ){

        MembershipApplicationResponseDto response = membershipApplicationService.apply(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Membership application submitted successfully",
                        response
                ));

    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserMembershipApplicationResponseDto>> getMembershipApplication(){

        UserMembershipApplicationResponseDto response = membershipApplicationService.getUserApplications();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        true,
                        "Application retrieved successfully",
                        response
                ));

    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MembershipApplicationSummaryResponseDto>>> getAllApplications(
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "appliedAt") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page,size, Sort.by(sortBy).descending());
        Page<MembershipApplicationSummaryResponseDto> response = membershipApplicationService.getApplications(status,pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        true,
                        "Membership applications retrieved successfully",
                        response
                ));


    }


}
