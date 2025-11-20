package com.pds.location.controller;

import com.pds.location.service.DistanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for computing distance between two coordinate pairs.
 * Responsibilities:
 *   - Exposes /api/distance for LogisticsService to rank vehicles
 *   - Delegates distance computation to DistanceService
 *   - Returns distance in kilometers
 * Integration:
 *   - DistanceService (Google Routes + Haversine fallback)
 *   - LogisticsService → VehicleSelectionService
 */

@RestController
@RequestMapping("/api")
public class DistanceController {

    private final DistanceService distanceService;

    public DistanceController(DistanceService distanceService) {
        this.distanceService = distanceService;
    }

    // -------------------------------------------------------------
    // DISTANCE CALCULATION (lat/lon -> distance in KM)
    // -------------------------------------------------------------
    @GetMapping("/distance")
    public double getDistance(
            @RequestParam double lat1,
            @RequestParam double lng1,
            @RequestParam double lat2,
            @RequestParam double lng2
    ) {
        return distanceService.computeDistance(lat1, lng1, lat2, lng2);
    }
}
