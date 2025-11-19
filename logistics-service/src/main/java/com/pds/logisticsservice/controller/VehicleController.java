package com.pds.logisticsservice.controller;

import com.pds.logisticsservice.model.VehicleLocationUpdate;
import com.pds.logisticsservice.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
    REST API for vehicle-related operations. Mostly used for updating real-time vehicle locations
 */

//TODO: add endpoint to register a new vehicle, assign to MAINTENANCE, etc.
//TODO: Add endpoint to get all vehicles or by status

@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    //==================================================================================
    // Updates the location of a specific vehicle by POST /vehicle/update
    //=================================================================================
    @PostMapping("/update")
    public String updateLocation(@RequestBody VehicleLocationUpdate req) {
        vehicleService.updateLocation(req);
        return "Location updated";
    }
}
