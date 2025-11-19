package com.pds.logisticsservice.repository;

import com.pds.logisticsservice.model.Vehicle;
import com.pds.logisticsservice.model.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
    Repository interface for managing vehicle persistence
    (Spring Data JPA automatically implements this interface at runtime)

    USED BY:
        VehicleService.java, VehicleSelectionService.java
 */

//TODO: add query for finding vehicles within a radius

public interface VehicleRepository extends  JpaRepository <Vehicle, Long>{

    // Returns all vehicles matching the provided status (e.g. AVAILABLE)
    List<Vehicle> findByStatus (VehicleStatus status);

}
