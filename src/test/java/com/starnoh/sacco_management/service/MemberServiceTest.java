package com.starnoh.sacco_management.service;

import com.starnoh.sacco_management.dto.MemberFilterRequest;
import com.starnoh.sacco_management.dto.MemberResponseDto;
import com.starnoh.sacco_management.dto.MemberSearchRequest;
import com.starnoh.sacco_management.dto.MemberSearchResponseDto;
import com.starnoh.sacco_management.entity.Members;
import com.starnoh.sacco_management.entity.Roles;
import com.starnoh.sacco_management.entity.Users;
import com.starnoh.sacco_management.enums.MemberStatus;
import com.starnoh.sacco_management.enums.RolesType;
import com.starnoh.sacco_management.exception.ForbiddenException;
import com.starnoh.sacco_management.repository.MemberRepository;
import com.starnoh.sacco_management.repository.MemberSearchSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private com.starnoh.sacco_management.repository.UsersRepository usersRepository;

    @InjectMocks
    private MemberService memberService;


    // Helper to create a mock Authentication with a single role (without the ROLE_ prefix)
    private Authentication mockAuth(String role) {
        Authentication auth = mock(Authentication.class);
        // Use doReturn to avoid generic capture issues with Mockito
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_" + role))).when(auth).getAuthorities();
        return auth;
    }

    @Test
    void search_shouldReturnResults_whenKeywordMatchesFirstName() {
        // Arrange
        Members m = new Members();
        m.setId(1L);
        Users u = new Users();
        u.setFirstName("John");
        u.setLastName("Doe");
        m.setUser(u);
        m.setStatus(MemberStatus.ACTIVE);

        Page<Members> repoPage = new PageImpl<>(Collections.singletonList(m), PageRequest.of(0,20), 1);

        try (MockedStatic<MemberSearchSpecification> mocked = mockStatic(MemberSearchSpecification.class)) {
            mocked.when(() -> MemberSearchSpecification.build(any())).thenReturn((Specification<Members>) (root, query, cb) -> cb.conjunction());

            when(memberRepository.findAll((Specification<Members>) any(), any(Pageable.class))).thenReturn(repoPage);

            MemberSearchRequest req = new MemberSearchRequest();
            req.setKeyword("John");
            req.setPage(0);
            req.setSize(20);
            req.setSortBy("createdAt");
            req.setDirection("DESC");

            Authentication auth = mockAuth("MEMBER");

            // Act
            Page<MemberSearchResponseDto> result = memberService.searchMembers(req, auth);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("John", result.getContent().get(0).getFirstName());

            verify(memberRepository).findAll((Specification<Members>) any(), any(Pageable.class));
        }
    }

    @Test
    void search_shouldHideNationalId_whenRoleIsMember() {
        // Arrange
        Members m = new Members();
        m.setId(2L);
        Users u = new Users();
        u.setFirstName("Jane");
        u.setLastName("Smith");
        u.setEmail("jane@example.com");
        u.setPhoneNumber("555-000");
        m.setUser(u);
        m.setNationalId("NAT-999");
        m.setStatus(MemberStatus.ACTIVE);

        Page<Members> repoPage = new PageImpl<>(Collections.singletonList(m), PageRequest.of(0,20), 1);

        try (MockedStatic<MemberSearchSpecification> mocked = mockStatic(MemberSearchSpecification.class)) {
            mocked.when(() -> MemberSearchSpecification.build(any())).thenReturn((Specification<Members>) (root, query, cb) -> cb.conjunction());

            when(memberRepository.findAll((Specification<Members>) any(), any(Pageable.class))).thenReturn(repoPage);

            MemberSearchRequest req = new MemberSearchRequest();
            req.setKeyword("Jane");
            req.setPage(0);
            req.setSize(20);
            req.setSortBy("createdAt");
            req.setDirection("DESC");

            Authentication auth = mockAuth("MEMBER");

            // Act
            Page<MemberSearchResponseDto> result = memberService.searchMembers(req, auth);

            // Assert: member role should not see nationalId
            assertNotNull(result);
            MemberSearchResponseDto dto = result.getContent().get(0);
            assertEquals("Jane", dto.getFirstName());
            assertNull(dto.getNationalId(), "NationalId should be hidden for MEMBER role");
        }
    }

    @Test
    void search_shouldIgnoreNationalIdFilter_whenRoleIsNotAdmin() {
        // Arrange
        Members m = new Members();
        m.setId(3L);
        Users u = new Users();
        u.setFirstName("Alice");
        m.setUser(u);
        m.setStatus(MemberStatus.ACTIVE);

        Page<Members> repoPage = new PageImpl<>(Collections.singletonList(m), PageRequest.of(0,20), 1);

        try (MockedStatic<MemberSearchSpecification> mocked = mockStatic(MemberSearchSpecification.class)) {
            // Capture the request passed into the static build method and assert its nationalId is null
            mocked.when(() -> MemberSearchSpecification.build(any())).thenAnswer(invocation -> {
                Object passed = invocation.getArgument(0);
                // We return a noop specification regardless
                return (Specification<Members>) (root, query, cb) -> cb.conjunction();
            });

            when(memberRepository.findAll((Specification<Members>) any(), any(Pageable.class))).thenReturn(repoPage);

            MemberSearchRequest req = new MemberSearchRequest();
            req.setKeyword("Alice");
            req.setNationalId("SHOULD_BE_IGNORED");
            req.setPage(0);
            req.setSize(20);
            req.setSortBy("createdAt");
            req.setDirection("DESC");

            Authentication auth = mockAuth("MEMBER");

            // Act
            memberService.searchMembers(req, auth);

            // Verify that build was called with a request whose nationalId was cleared by the service
            mocked.verify(() -> MemberSearchSpecification.build(argThat(r -> ((MemberSearchRequest) r).getNationalId() == null)));
        }
    }

    @Test
    void search_shouldReturnEmptyPage_whenNoMembersMatch() {
        // Arrange
        Page<Members> empty = Page.empty();

        try (MockedStatic<MemberSearchSpecification> mocked = mockStatic(MemberSearchSpecification.class)) {
            mocked.when(() -> MemberSearchSpecification.build(any())).thenReturn((Specification<Members>) (root, query, cb) -> cb.conjunction());
            when(memberRepository.findAll((Specification<Members>) any(), any(Pageable.class))).thenReturn(empty);

            MemberSearchRequest req = new MemberSearchRequest();
            req.setKeyword("DoesNotExist");
            req.setPage(0);
            req.setSize(20);
            req.setSortBy("createdAt");
            req.setDirection("DESC");

            Authentication auth = mockAuth("ADMINISTRATOR");

            // Act
            Page<MemberSearchResponseDto> result = memberService.searchMembers(req, auth);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
        }
    }


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
