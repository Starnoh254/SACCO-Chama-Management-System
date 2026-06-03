package com.starnoh.sacco_management.service;

import com.starnoh.sacco_management.entity.Users;
import com.starnoh.sacco_management.enums.UserStatus;
import com.starnoh.sacco_management.exception.ForbiddenException;
import com.starnoh.sacco_management.exception.ResourceNotFoundException;
import com.starnoh.sacco_management.exception.UnauthorizedException;
import com.starnoh.sacco_management.repository.UsersRepository;
import com.starnoh.sacco_management.util.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UsersRepository usersRepository;
    private final SecurityUtils securityUtils;

    public CurrentUserService(UsersRepository usersRepository, SecurityUtils securityUtils) {
        this.usersRepository = usersRepository;
        this.securityUtils = securityUtils;
    }

    /**
     * Helper method to fetch, validate, and return the currently authenticated user.
     */
    public Users getValidatedCurrentUser() {
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
}
