package com.starnoh.sacco_management.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Roles role;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "last_login" , nullable = false , updatable = false)
    private Instant lastLogin;

    @Column(name = "created_at" , nullable = false , updatable = false)
    private Instant createdAt;
}
