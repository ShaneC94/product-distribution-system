package com.pds.logisticsservice.service;

import com.pds.logisticsservice.model.OrderRequest;
import com.pds.logisticsservice.model.Vehicle;
import com.pds.logisticsservice.model.VehicleStatus;
import com.pds.logisticsservice.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/*
    THIS IS THE MAIN COMPONENT OF THE SERVICE

    Handles core assignment logic for the best vehicle with the following steps:
        1. Retrieve all AVAILABLE vehicles
        2. Filter vehicles by capacity
        3. Request Distance Calculation from location-service
        4. Select the closest vehicle after filtering
        5. Change status of Vehicle to EN_ROUTE and update capacityRemaining

    INTEGRATION POINTS:
        location-service -> calculates distance
        order-processing-service -> requests the assignment
 */

//TODO: add distributed lock to prevent double-assignment
//TODO: add load-balancing, retries if location-service is down, alternative selection algorithms

@Service
public class VehicleSelectionService {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DistanceServiceClient distanceClient;

    //===========================================================================================
    // Assigns best available vehicle based on distance and capacity
    //==========================================================================================
    public Vehicle assignVehicle(OrderRequest order) {

        // Get all AVAILABLE vehicles
        List<Vehicle> available =
                vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);

        //Filter based on which vehicles have enough capacity for the product and add that to list
        List<Vehicle> capable =
                available.stream()
                        .filter(v -> v.getCapacityRemaining() >= order.getVolume())
                        .toList();

        // Ensure at least one valid vehicle exists
        if (capable.isEmpty()) {
            throw new RuntimeException("No capable vehicle available.");
        }

        // Out of all AVAILABLE and capable vehicles, select the vehicle that is closest
        Vehicle best = capable.stream()
                .min(Comparator.comparingDouble(v ->
                        distanceClient.getDistance(
                                v.getLat(), v.getLng(),
                                order.getPickupLat(), order.getPickupLng())))
                .orElseThrow(() ->
                        new RuntimeException("No capable vehicle available."));

        // when vehicle is selected, update its status and capacity
        best.setStatus(VehicleStatus.EN_ROUTE);
        best.setCapacityRemaining(
                best.getCapacityRemaining() - order.getVolume());

        vehicleRepository.save(best);

        return best;
    }
}
