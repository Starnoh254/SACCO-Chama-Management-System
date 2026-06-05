package com.starnoh.sacco_management.service;

import com.starnoh.sacco_management.dto.MemberFilterRequest;
import com.starnoh.sacco_management.dto.MemberResponseDto;
import com.starnoh.sacco_management.dto.UpdateMemberRequest;
import com.starnoh.sacco_management.entity.Members;
import com.starnoh.sacco_management.entity.Users;
import com.starnoh.sacco_management.enums.MemberStatus;
import com.starnoh.sacco_management.exception.ForbiddenException;
import com.starnoh.sacco_management.exception.ResourceNotFoundException;
import com.starnoh.sacco_management.repository.MemberRepository;
import com.starnoh.sacco_management.repository.MemberSpecification;
import com.starnoh.sacco_management.repository.UsersRepository;
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
    private final UsersRepository usersRepository;

    public MemberService(CurrentUserService currentUserService, MemberRepository memberRepository, UsersRepository usersRepository) {
        this.currentUserService = currentUserService;
        this.memberRepository = memberRepository;
        this.usersRepository = usersRepository;
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

    public MemberResponseDto updateMember(Long id, UpdateMemberRequest request) {
        Users admin = currentUserService.getValidatedCurrentUser();
        checkIfUserisAdminorTreasurer(admin);

        Members member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id : " + id));

        Users user = member.getUser();

        if(request.getNationalId() != null && !request.getNationalId().equals(member.getNationalId())) {
            memberRepository.findByNationalId(request.getNationalId()).ifPresent(m -> {
                    throw new ForbiddenException("Another member with the same National ID already exists");

            });
        }

        // 3. Apply partial updates — only fields that are not null
        if (request.getFirstName()   != null) user.setFirstName(request.getFirstName());
        if (request.getLastName()    != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());

        if (request.getNationalId()  != null) member.setNationalId(request.getNationalId());
        if (request.getAddress()     != null) member.setAddress(request.getAddress());
        if (request.getDateJoined()  != null) member.setDateJoined(request.getDateJoined());
        if (request.getStatus()      != null) member.setStatus(MemberStatus.valueOf(request.getStatus()));

        // 4. Persist — JPA dirty-checking saves only changed fields
        usersRepository.save(user);
        Members updatedMember = memberRepository.save(member);

        // 5. Return updated member as DTO
        return mapToDto(updatedMember);
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
