package com.pds.location.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "geocode_cache")
public class GeocodeCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "normalized_address", unique = true, nullable = false, length = 255)
    private String normalizedAddress;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_accessed", nullable = false)
    private LocalDateTime lastAccessed;

    public GeocodeCache() {}

    public GeocodeCache(String normalizedAddress, double latitude, double longitude) {
        this.normalizedAddress = normalizedAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = LocalDateTime.now();
        this.lastAccessed = LocalDateTime.now();
  }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastAccessed = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastAccessed = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getNormalizedAddress() { return normalizedAddress; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastAccessed() { return lastAccessed; }
}
