package com.pds.faultsreturnsservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class FaultReturn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String reason;

    @Enumerated(EnumType.STRING)
    private FaultReturnStatus status;

    private LocalDateTime reportedAt = LocalDateTime.now();

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public FaultReturnStatus getStatus() { return status; }
    public void setStatus(FaultReturnStatus status) { this.status = status; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }
}