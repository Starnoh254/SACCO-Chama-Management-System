package com.starnoh.sacco_management.service;

import com.starnoh.sacco_management.dto.LoginRequestDto;
import com.starnoh.sacco_management.dto.LoginResponseDto;
import com.starnoh.sacco_management.dto.RegisterRequestDto;
import com.starnoh.sacco_management.dto.UserResponseDto;
import com.starnoh.sacco_management.entity.Roles;
import com.starnoh.sacco_management.entity.Users;
import com.starnoh.sacco_management.enums.RolesType;
import com.starnoh.sacco_management.enums.UserStatus;
import com.starnoh.sacco_management.exception.DuplicateResourceException;
import com.starnoh.sacco_management.exception.ForbiddenException;
import com.starnoh.sacco_management.exception.ResourceNotFoundException;
import com.starnoh.sacco_management.exception.UnauthorizedException;
import com.starnoh.sacco_management.repository.RolesRepository;
import com.starnoh.sacco_management.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;
    private final JwtService jwtService;

    public AuthService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, RolesRepository rolesRepository, JwtService jwtService) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
        this.jwtService = jwtService;
    }

    public LoginResponseDto login(LoginRequestDto request){

        Users user = getUserByEmail(request.getEmail());

        boolean isPasswordValid = passwordEncoder.matches(request.getPassword() , user.getPasswordHash());

        if(!isPasswordValid){
            throw new UnauthorizedException("Invalid email or password");
        }

        if(user.getStatus() == UserStatus.SUSPENDED) {
            throw new ForbiddenException("Your account has been suspended. Please contact support.");
        }

        user.setLastLogin(Instant.now());

        String token = jwtService.generateToken(user);

        UserResponseDto userResponseDto = mapToUserResponseDto(user);

        return new LoginResponseDto(token , "Bearer" , userResponseDto);


    }

    public UserResponseDto register(RegisterRequestDto request){


            if(usersRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Registration failed : Email is already in use!");
            }

            Roles role = rolesRepository.findByName(String.valueOf(RolesType.USER)).orElseThrow(() -> new ResourceNotFoundException("Role not found"));

            String hashedPassword = passwordEncoder.encode(request.getPassword());
            Users user = new Users();
            user.setPasswordHash(hashedPassword);
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setRole(role);
            user.setPhoneNumber(request.getPhoneNumber());
            user.setEmail(request.getEmail());
            user.setStatus(UserStatus.ACTIVE);

            Users savedUser = usersRepository.save(user);

            return mapToUserResponseDto(savedUser);


    }

    private UserResponseDto mapToUserResponseDto(Users user) {

        return new UserResponseDto(
                user.getId(),
                user.getRole().getName(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getStatus().name(),
                user.getLastLogin(),
                user.getCreatedAt()
        );
    }

    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }


}
