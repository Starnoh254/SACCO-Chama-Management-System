package com.starnoh.sacco_management.controller;

import com.starnoh.sacco_management.dto.ApiResponse;
import com.starnoh.sacco_management.dto.MemberFilterRequest;
import com.starnoh.sacco_management.dto.MemberResponseDto;
import com.starnoh.sacco_management.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'TREASURER')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MemberResponseDto>>> getAllMembers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        MemberFilterRequest filter = MemberFilterRequest.builder()
                .status(status)
                .search(search)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .build();

        Page<MemberResponseDto> response = memberService.getAllMembers(filter);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched members successfully", response));
    }
}
