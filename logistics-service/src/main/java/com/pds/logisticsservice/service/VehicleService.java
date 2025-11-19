package com.pds.logisticsservice.service;

import com.pds.logisticsservice.model.Vehicle;
import com.pds.logisticsservice.model.VehicleLocationUpdate;
import com.pds.logisticsservice.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
    Handles updates to vehicle state (GPS, status, etc.)

    This service is invoked by VehicleController when location updates are pushed from vehicles
 */

//TODO: add logging for debugging real-time updates
//TODO: add validation to ensure vehicle exists before updating

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository repository;

    // =====================================================================================
    // Updates current GPS location of a vehicle
    //======================================================================================
    public void updateLocation (VehicleLocationUpdate req) {
        // Retrieve vehicle from database
        Vehicle v = repository.findById(req.getVehicleId()).orElseThrow();

        v.setLat(req.getLat()); //update coordinates
        v.setLng(req.getLng());
        repository.save(v); //save changes to database

    }
}
