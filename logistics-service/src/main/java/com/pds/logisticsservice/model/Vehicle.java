package com.pds.logisticsservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

/*
    Represents a delivery vehicle stored in the logisitics service database to keep track of
    location, remaining capacity and operational status

    INTEGRATION POINTS:
        order-processing-service: requests vehicle assignment
        location-service: used indirectly when computing distances from vehicles to pickup points
*/

//TODO: add driverName, speed, lastUpdated timestamp
//TODO: add validation (e.g. capacityRemaining cannot be negative)

@Entity
public class Vehicle {

    @Id
    private Long id; //Unique ID (primary key)

    private double lat, lng; //current longtitude and latitude

    private double capacityMax, capacityRemaining; //max and remaining capacity

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    // ----- GETTERS & SETTERS -----
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getCapacityMax() {
        return capacityMax;
    }

    public void setCapacityMax(double capacityMax) {
        this.capacityMax = capacityMax;
    }

    public double getCapacityRemaining() {
        return capacityRemaining;
    }

    public void setCapacityRemaining(double capacityRemaining) {
        this.capacityRemaining = capacityRemaining;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }
}
