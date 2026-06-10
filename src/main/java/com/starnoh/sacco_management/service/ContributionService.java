package com.starnoh.sacco_management.service;

import com.starnoh.sacco_management.dto.ContributionResponseDto;
import com.starnoh.sacco_management.dto.RecordContributionRequest;
import com.starnoh.sacco_management.entity.Contributions;
import com.starnoh.sacco_management.entity.Members;
import com.starnoh.sacco_management.entity.Users;
import com.starnoh.sacco_management.enums.ContributionStatus;
import com.starnoh.sacco_management.enums.PaymentMethod;
import com.starnoh.sacco_management.exception.BadRequestException;
import com.starnoh.sacco_management.exception.DuplicateResourceException;
import com.starnoh.sacco_management.exception.ResourceNotFoundException;
import com.starnoh.sacco_management.repository.ContributionsRepository;
import com.starnoh.sacco_management.repository.MemberRepository;
import com.starnoh.sacco_management.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionsRepository contributionsRepository;
    private final MemberRepository memberRepository;
    private final UsersRepository usersRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ContributionResponseDto recordContribution(
            RecordContributionRequest request,
            Authentication authentication
    ) {

        Users treasurer = currentUserService.getValidatedCurrentUser();

        // validate member exists
        Members member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));

        // validate member is ACTIVE
        if(!"ACTIVE".equalsIgnoreCase(member.getStatus().toString())){
            throw new BadRequestException("Cannot record contribution for a suspended member");
        }

        // Duplicate check - same member + same date + same amount
        LocalDate contribDate = request.getContributionDate() != null ? request.getContributionDate() : LocalDate.now();

        if(contributionsRepository.existsByMemberIdAndContributionDateAndAmount(request.getMemberId() , contribDate , request.getAmount())){
            throw new DuplicateResourceException("A contribution of " + request.getAmount() + " for this member on " + contribDate + " already exists.");
        }

        // Resolve the Treasure's user ID for audit trail
        Long recordedById = treasurer.getId();

        // Build the contribution entity

        Contributions contributions = new Contributions();
        contributions.setMember(member);
        contributions.setAmount(request.getAmount());
        contributions.setContributionDate(contribDate);
        contributions.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
        contributions.setReferenceNumber(request.getReferenceNumber());
        contributions.setStatus(
                request.getStatus() != null ? ContributionStatus.valueOf(request.getStatus()) : ContributionStatus.PAID
        );

        // save
        Contributions saved = contributionsRepository.save(contributions);

        // return response DTO
        return toResponseDto(saved , member);


    }

    private ContributionResponseDto toResponseDto(
            Contributions contribution , Members member
    ) {
        Users user = member.getUser();
        return ContributionResponseDto.builder()
                .id(contribution.getId())
                .memberId(member.getId())
                .membershipNumber(member.getMembershipNumber())
                .memberName(user.getFirstName() + " " + user.getLastName())
                .amount(contribution.getAmount())
                .contributionDate(contribution.getContributionDate())
                .paymentMethod(contribution.getPaymentMethod().toString())
                .referenceNumber(contribution.getReferenceNumber())
                .status(contribution.getStatus().toString())
                .createdAt(contribution.getCreatedAt())
                .build();
    }
}
