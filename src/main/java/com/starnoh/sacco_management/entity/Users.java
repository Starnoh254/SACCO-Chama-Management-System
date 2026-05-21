package com.starnoh.sacco_management.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long role_id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;

    private String phone_number;

    private String password_hash;

    private String status;

    @Column(name = "last_login" , nullable = false , updatable = false)
    private Instant lastLogin;

    @Column(name = "created_at" , nullable = false , updatable = false)
    private Instant createdAt;
}
