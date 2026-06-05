package com.starnoh.sacco_management.controller;

import com.starnoh.sacco_management.dto.ApiResponse;
import com.starnoh.sacco_management.dto.MemberFilterRequest;
import com.starnoh.sacco_management.dto.MemberResponseDto;
import com.starnoh.sacco_management.dto.UpdateMemberRequest;
import com.starnoh.sacco_management.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // Endpoint to fetch paginated and filtered list of members, accessible only to ADMINISTRATOR and TREASURER roles

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

    @PreAuthorize(("hasAnyRole('ADMINISTRATOR', 'TREASURER')"))
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponseDto>> getMemberById(@PathVariable Long id) {
        MemberResponseDto member = memberService.getMemberById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched member successfully", member));
    }


    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/members/{id}")
    public ResponseEntity<ApiResponse<MemberResponseDto>> updateMember(@PathVariable Long id, @Valid @RequestBody UpdateMemberRequest request) {
        MemberResponseDto updatedMember = memberService.updateMember(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Member updated successfully", updatedMember));
    }
}
