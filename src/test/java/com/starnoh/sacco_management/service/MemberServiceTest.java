package com.starnoh.sacco_management.service;

import com.starnoh.sacco_management.dto.MemberFilterRequest;
import com.starnoh.sacco_management.dto.MemberResponseDto;
import com.starnoh.sacco_management.entity.Members;
import com.starnoh.sacco_management.entity.Roles;
import com.starnoh.sacco_management.entity.Users;
import com.starnoh.sacco_management.enums.MemberStatus;
import com.starnoh.sacco_management.enums.RolesType;
import com.starnoh.sacco_management.exception.ForbiddenException;
import com.starnoh.sacco_management.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    void getAllMembers_whenUserNotAdminOrTreasurer_throwsForbidden() {
        Users user = new Users();
        Roles role = new Roles();
        role.setName("MEMBER");
        user.setRole(role);

        when(currentUserService.getValidatedCurrentUser()).thenReturn(user);

        MemberFilterRequest filter = MemberFilterRequest.builder()
                .page(0)
                .size(20)
                .sortBy("createdAt")
                .direction("DESC")
                .build();

        assertThrows(ForbiddenException.class, () -> memberService.getAllMembers(filter));

        verify(currentUserService).getValidatedCurrentUser();
        verifyNoInteractions(memberRepository);
    }

    @Test
    void getAllMembers_whenAdmin_returnsMappedPage() {
        Users admin = new Users();
        Roles role = new Roles();
        role.setName(RolesType.ADMINISTRATOR.toString());
        admin.setRole(role);

        Members m = new Members();
        m.setId(1L);
        Users u = new Users();
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setEmail("john@example.com");
        u.setPhoneNumber("123456789");
        m.setUser(u);
        m.setMembershipNumber("MEM-2026-000001");
        m.setNationalId("12345");
        m.setDateJoined(LocalDate.now());
        m.setAddress("Some Street");
        m.setStatus(MemberStatus.ACTIVE);
        m.setCreatedAt(Instant.now());

        Page<Members> repoPage = new PageImpl<>(Collections.singletonList(m), PageRequest.of(0,20), 1);

        when(currentUserService.getValidatedCurrentUser()).thenReturn(admin);
        when(memberRepository.findAll((org.springframework.data.jpa.domain.Specification<Members>) any(), any(Pageable.class))).thenReturn(repoPage);

        MemberFilterRequest filter = MemberFilterRequest.builder()
                .page(0)
                .size(20)
                .sortBy("createdAt")
                .direction("DESC")
                .build();

        Page<MemberResponseDto> result = memberService.getAllMembers(filter);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        MemberResponseDto dto = result.getContent().get(0);
        assertEquals("John", dto.getFirstName());
        assertEquals("MEM-2026-000001", dto.getMembershipNumber());

        verify(currentUserService).getValidatedCurrentUser();
        verify(memberRepository).findAll((org.springframework.data.jpa.domain.Specification<Members>) any(), any(Pageable.class));
    }
}
