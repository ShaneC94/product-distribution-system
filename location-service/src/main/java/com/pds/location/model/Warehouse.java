package com.pds.location.model;

import jakarta.persistence.*;

/**
 * JPA Entity representing a physical warehouse in the database.
 * Stores:
 *   - Name and address (unique by name)
 *   - Latitude & Longitude coordinates
 * Used By:
 *   - LocationService (distance calculation, ranking)
 *   - WarehouseController (CRUD operations)
 *   - LogisticsService (route lookup via LocationService)
 */

@Entity
@Table(
        name = "warehouses",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name"})
        }
)
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String address;
    private double latitude;
    private double longitude;

    public Warehouse() {}

    public Warehouse(String name, String address, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
