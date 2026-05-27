package com.starnoh.sacco_management.entity;

import com.starnoh.sacco_management.enums.ApplicationStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "membership_applications")
public class MembershipApplications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id" , nullable = false , unique = true)
    private Users user;


    @Column(name = "national_id" , nullable = false , length = 50)
    private String nationalId;


    @Column(columnDefinition = "TEXT")
    private String address;


    @Enumerated(EnumType.STRING)
    @Column(name = "application_status" , nullable = false)
    private ApplicationStatus applicationStatus;


    @Column(name = "applied_at" , nullable = false , updatable = false)
    private Instant appliedAt;

    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private Users reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @PrePersist
    public void prePersist() {
        appliedAt = Instant.now();

        if (applicationStatus == null) {
            applicationStatus = ApplicationStatus.PENDING;
        }
    }
}
