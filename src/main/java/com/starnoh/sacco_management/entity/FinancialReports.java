package com.starnoh.sacco_management.entity;

import com.starnoh.sacco_management.enums.ReportType;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "financial_reports")
public class FinancialReports {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "generated_by", nullable = false)
    private Users generatedBy;

    @Column(name = "report_type", length = 50, nullable = false)
    private ReportType reportType;

    @Column(name = "report_period", length = 50, nullable = false)
    private String reportPeriod;


    @Column(name = "file_path", length = 50, nullable = false)
    private String filePath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();

    }
}
