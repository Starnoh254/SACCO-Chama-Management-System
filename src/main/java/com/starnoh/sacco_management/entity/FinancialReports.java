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

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", length = 50, nullable = false)
    private ReportType reportType;

    @Column(name = "report_period", length = 50, nullable = false)
    private String reportPeriod;


    @Column(name = "file_path", length = 255, nullable = false)
    private String filePath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(Users generatedBy) {
        this.generatedBy = generatedBy;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public String getReportPeriod() {
        return reportPeriod;
    }

    public void setReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
