package com.starnoh.sacco_management.service;

import com.starnoh.sacco_management.dto.MembershipApplicationRequestDto;
import com.starnoh.sacco_management.dto.MembershipApplicationResponseDto;
import com.starnoh.sacco_management.dto.UserMembershipApplicationResponseDto;
import com.starnoh.sacco_management.entity.MembershipApplications;
import com.starnoh.sacco_management.entity.Users;
import com.starnoh.sacco_management.enums.ApplicationStatus;
import com.starnoh.sacco_management.enums.UserStatus;
import com.starnoh.sacco_management.exception.DuplicateResourceException;
import com.starnoh.sacco_management.exception.ForbiddenException;
import com.starnoh.sacco_management.exception.ResourceNotFoundException;
import com.starnoh.sacco_management.exception.UnauthorizedException;
import com.starnoh.sacco_management.repository.MembershipApplicationsRepository;
import com.starnoh.sacco_management.repository.UsersRepository;
import com.starnoh.sacco_management.util.SecurityUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class MembershipApplicationService {

    private final UsersRepository usersRepository;
    private final SecurityUtils securityUtils;
    private final MembershipApplicationsRepository membershipApplicationsRepository;

    public MembershipApplicationService(UsersRepository usersRepository, SecurityUtils securityUtils, MembershipApplicationsRepository membershipApplicationsRepository) {
        this.usersRepository = usersRepository;
        this.securityUtils = securityUtils;
        this.membershipApplicationsRepository = membershipApplicationsRepository;
    }

    public UserMembershipApplicationResponseDto getUserApplications(){
        // Use the helper method to get the validated user
        Users user = getValidatedCurrentUser();

        MembershipApplications membershipApplication = membershipApplicationsRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Membership Application not found"));

        return mapToUserMembershipApplicationResponseDto(membershipApplication);
    }

    @Transactional(rollbackFor = Exception.class)
    public MembershipApplicationResponseDto apply(MembershipApplicationRequestDto request) {
        // Use the helper method to get the validated user
        Users user = getValidatedCurrentUser();

        boolean exists =
                membershipApplicationsRepository
                        .existsByUserIdAndApplicationStatus(
                                user.getId(),
                                ApplicationStatus.PENDING
                        );

        if(exists){
            throw new DuplicateResourceException(
                    "You already have a pending application"
            );
        }

        MembershipApplications membershipApplications = new MembershipApplications();
        membershipApplications.setApplicationStatus(ApplicationStatus.PENDING);
        membershipApplications.setAppliedAt(Instant.now());
        membershipApplications.setAddress(request.getAddress());
        membershipApplications.setNationalId(request.getNationalId());
        membershipApplications.setUser(user);

        membershipApplicationsRepository.save(membershipApplications);

        return mapToMembershipApplicationResponseDto(membershipApplications);
    }

    /**
     * Helper method to fetch, validate, and return the currently authenticated user.
     */
    private Users getValidatedCurrentUser() {
        Long userId = securityUtils.getCurrentUserId();

        if(userId == null) {
            throw new UnauthorizedException("User Id not found");
        }

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + userId));

        if(user.getStatus() == UserStatus.SUSPENDED) {
            throw new ForbiddenException("Your account has been suspended. Please contact support.");
        }

        return user;
    }

    private MembershipApplicationResponseDto mapToMembershipApplicationResponseDto(MembershipApplications membershipApplication) {
        return new MembershipApplicationResponseDto(
                membershipApplication.getId(),
                membershipApplication.getApplicationStatus().toString(),
                membershipApplication.getAppliedAt()
        );
    }

    private UserMembershipApplicationResponseDto mapToUserMembershipApplicationResponseDto(MembershipApplications membershipApplications) {
        return new UserMembershipApplicationResponseDto(
                membershipApplications.getId(),
                membershipApplications.getNationalId(),
                membershipApplications.getAddress(),
                membershipApplications.getApplicationStatus().toString(),
                membershipApplications.getAppliedAt(),
                membershipApplications.getReviewedBy(),
                membershipApplications.getReviewedAt()
        );
    }
}
