package com.starnoh.sacco_management.service;

import com.starnoh.sacco_management.dto.*;
import com.starnoh.sacco_management.entity.MembershipApplications;
import com.starnoh.sacco_management.entity.Roles;
import com.starnoh.sacco_management.entity.Users;
import com.starnoh.sacco_management.enums.ApplicationStatus;
import com.starnoh.sacco_management.enums.RolesType;
import com.starnoh.sacco_management.enums.UserStatus;
import com.starnoh.sacco_management.exception.*;
import com.starnoh.sacco_management.repository.MembershipApplicationsRepository;
import com.starnoh.sacco_management.repository.RolesRepository;
import com.starnoh.sacco_management.repository.UsersRepository;
import com.starnoh.sacco_management.util.SecurityUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
public class MembershipApplicationService {

    private final UsersRepository usersRepository;
    private final SecurityUtils securityUtils;
    private final MembershipApplicationsRepository membershipApplicationsRepository;
    private final RolesRepository rolesRepository;

    public MembershipApplicationService(UsersRepository usersRepository, SecurityUtils securityUtils, MembershipApplicationsRepository membershipApplicationsRepository, RolesRepository rolesRepository) {
        this.usersRepository = usersRepository;
        this.securityUtils = securityUtils;
        this.membershipApplicationsRepository = membershipApplicationsRepository;
        this.rolesRepository = rolesRepository;
    }

    @Transactional
    public MembershipApplicationApprovalResponseDto rejectApplication(Long applicationId){

        Users admin = getValidatedCurrentUser();

        checkIfUserisAdmin(admin);

        MembershipApplications application = membershipApplicationsRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership Application not found"));

        if(application.getApplicationStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Only pending applications can be rejected");
        }

        application.setApplicationStatus(ApplicationStatus.REJECTED);

        application.setReviewedBy(admin);
        application.setReviewedAt(Instant.now());


        membershipApplicationsRepository.save(application);

        return mapToMembershipApplicationApprovalResponseDto(application);

    }

    @Transactional
    public MembershipApplicationApprovalResponseDto approveApplication(Long applicationId){

        Users admin = getValidatedCurrentUser();

        checkIfUserisAdmin(admin);

        MembershipApplications application = membershipApplicationsRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership Application not found"));

        if(application.getApplicationStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Only pending applications can be approved");
        }

        application.setApplicationStatus(ApplicationStatus.APPROVED);

        application.setReviewedBy(admin);
        application.setReviewedAt(Instant.now());

        Roles memberRole = rolesRepository.findByName(RolesType.MEMBER.toString())
                .orElseThrow(() -> new ResourceNotFoundException("The role selected doesn't exist "));

        Users applicant = application.getUser();

        applicant.setRole(memberRole);

        usersRepository.save(applicant);

        membershipApplicationsRepository.save(application);

        return mapToMembershipApplicationApprovalResponseDto(application);

    }

    public Page<MembershipApplicationSummaryResponseDto> getApplications(String status , Pageable pageable) {
        Users user = getValidatedCurrentUser();

        checkIfUserisAdmin(user);

        // Convert the incoming String parameter into the strict Enum type
        ApplicationStatus statusEnum = ApplicationStatus.valueOf(status.toUpperCase());
        Page<MembershipApplications>  membershipApplicationsPage = membershipApplicationsRepository.findByApplicationStatus(statusEnum,pageable);

        return membershipApplicationsPage.map(this::mapToMembershipApplicationSummaryResponseDto);


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

    private void checkIfUserisAdmin(Users user) {
        if(!Objects.equals(user.getRole().getName(), "ADMINISTRATOR")){
            throw new ForbiddenException("You do not have permission to access membership applications");
        }
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

    private MembershipApplicationSummaryResponseDto mapToMembershipApplicationSummaryResponseDto(MembershipApplications membershipApplications) {
        return new MembershipApplicationSummaryResponseDto(
                membershipApplications.getId(),
                membershipApplications.getUser().getId(),
                membershipApplications.getUser().getFirstName(),
                membershipApplications.getUser().getLastName(),
                membershipApplications.getUser().getEmail(),
                membershipApplications.getUser().getPhoneNumber(),
                membershipApplications.getNationalId(),
                membershipApplications.getAddress(),
                membershipApplications.getApplicationStatus().toString(),
                membershipApplications.getAppliedAt()
        );
    }

    private MembershipApplicationApprovalResponseDto mapToMembershipApplicationApprovalResponseDto(MembershipApplications membershipApplications){
        return new MembershipApplicationApprovalResponseDto(
                membershipApplications.getId(),
                membershipApplications.getApplicationStatus().toString(),
                membershipApplications.getReviewedBy().getId(),
                membershipApplications.getReviewedAt()
        );
    }
}
