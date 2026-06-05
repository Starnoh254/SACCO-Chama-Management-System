package com.starnoh.sacco_management.service;

import com.starnoh.sacco_management.dto.MemberFilterRequest;
import com.starnoh.sacco_management.dto.MemberResponseDto;
import com.starnoh.sacco_management.entity.Members;
import com.starnoh.sacco_management.entity.Users;
import com.starnoh.sacco_management.exception.ForbiddenException;
import com.starnoh.sacco_management.exception.ResourceNotFoundException;
import com.starnoh.sacco_management.repository.MemberRepository;
import com.starnoh.sacco_management.repository.MemberSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MemberService {

    private final CurrentUserService currentUserService;
    private final MemberRepository memberRepository;

    public MemberService(CurrentUserService currentUserService, MemberRepository memberRepository) {
        this.currentUserService = currentUserService;
        this.memberRepository = memberRepository;
    }


    // Method to fetch paginated and filtered list of members
    public Page<MemberResponseDto> getAllMembers(MemberFilterRequest filter) {

        Users admin = currentUserService.getValidatedCurrentUser();
        checkIfUserisAdminorTreasurer(admin);

        Sort sort = Sort.by("ASC".equalsIgnoreCase(filter.getDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC, filter.getSortBy());
        Pageable pageable = PageRequest.of(filter.getPage(), Math.min(filter.getSize() , 100), sort);

        Specification<Members> spec = MemberSpecification.build(filter);
        Page<Members> membersPage = memberRepository.findAll(spec, pageable);

        return membersPage.map(this::mapToDto);

    }


    // Method to fetch a single member by ID
    public MemberResponseDto getMemberById(Long id) {
        Users admin = currentUserService.getValidatedCurrentUser();
        checkIfUserisAdminorTreasurer(admin);

        Members member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id : " + id));

        return mapToDto(member);
    }

    // Helper method to check if the user has ADMINISTRATOR or TREASURER role
    private void checkIfUserisAdminorTreasurer(Users user) {
        if(!Objects.equals(user.getRole().getName(), "ADMINISTRATOR") && !Objects.equals(user.getRole().getName(), "TREASURER")) {
            throw new ForbiddenException("You do not have permission to access Members data");
        }
    }

    // Helper method to convert Members entity to MemberResponseDto
    private MemberResponseDto mapToDto(Members member) {
        return new MemberResponseDto(
                member.getId(),
                member.getMembershipNumber(),
                member.getUser().getFirstName(),
                member.getUser().getLastName(),
                member.getUser().getEmail(),
                member.getUser().getPhoneNumber(),
                member.getNationalId(),
                member.getDateJoined(),
                member.getStatus().name(),
                member.getAddress(),
                member.getCreatedAt()
        );
    }
}
