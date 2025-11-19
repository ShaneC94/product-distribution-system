package com.pds.logisticsservice.controller;

import com.pds.logisticsservice.model.OrderRequest;
import com.pds.logisticsservice.model.Vehicle;
import com.pds.logisticsservice.service.VehicleSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
    REST API for handling delivery vehicle assignment

    Called directly by order-processing-service when a new order is created
 */

//TODO: add logging, send confirmation back to order-processing-service

@RestController
@RequestMapping("/assignment")
public class AssignmentController {

    @Autowired
    private VehicleSelectionService selectionService;

    //==================================================================================
    // Assigns the best vehicle to the incoming order request via POST /assignment/assign
    //=================================================================================
    @PostMapping("/assign")
    public Vehicle assignVehicle(@RequestBody OrderRequest order) {
        return selectionService.assignVehicle(order);
    }
}
