package com.starnoh.sacco_management.service;

import com.starnoh.sacco_management.entity.Users;
import com.starnoh.sacco_management.enums.UserStatus;
import com.starnoh.sacco_management.exception.ForbiddenException;
import com.starnoh.sacco_management.exception.ResourceNotFoundException;
import com.starnoh.sacco_management.exception.UnauthorizedException;
import com.starnoh.sacco_management.repository.UsersRepository;
import com.starnoh.sacco_management.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private CurrentUserService currentUserService;

    @Test
    void getValidatedCurrentUser_whenUserIdMissing_throwsUnauthorized() {
        when(securityUtils.getCurrentUserId()).thenReturn(null);

        assertThrows(UnauthorizedException.class, () -> currentUserService.getValidatedCurrentUser());

        verify(securityUtils).getCurrentUserId();
        verifyNoMoreInteractions(securityUtils, usersRepository);
    }

    @Test
    void getValidatedCurrentUser_whenUserNotFound_throwsResourceNotFound() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> currentUserService.getValidatedCurrentUser());

        verify(securityUtils).getCurrentUserId();
        verify(usersRepository).findById(1L);
    }

    @Test
    void getValidatedCurrentUser_whenUserSuspended_throwsForbidden() {
        when(securityUtils.getCurrentUserId()).thenReturn(2L);

        Users suspended = new Users();
        suspended.setId(2L);
        suspended.setStatus(UserStatus.SUSPENDED);

        when(usersRepository.findById(2L)).thenReturn(Optional.of(suspended));

        assertThrows(ForbiddenException.class, () -> currentUserService.getValidatedCurrentUser());

        verify(securityUtils).getCurrentUserId();
        verify(usersRepository).findById(2L);
    }

    @Test
    void getValidatedCurrentUser_whenUserActive_returnsUser() {
        when(securityUtils.getCurrentUserId()).thenReturn(3L);

        Users active = new Users();
        active.setId(3L);
        active.setStatus(UserStatus.ACTIVE);

        when(usersRepository.findById(3L)).thenReturn(Optional.of(active));

        Users result = currentUserService.getValidatedCurrentUser();

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(UserStatus.ACTIVE, result.getStatus());

        verify(securityUtils).getCurrentUserId();
        verify(usersRepository).findById(3L);
    }
}
