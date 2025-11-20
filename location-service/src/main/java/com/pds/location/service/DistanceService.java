package com.pds.location.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * Provides distance calculation functionality using Google Maps Routes API.
 * Integration:
 *   - Primary: GoogleMapsService.computeRoute(...) for accurate driving distance
 *   - Fallback: Haversine formula for geographic straight-line distance
 * Used By:
 *   - DistanceController (REST endpoint /api/distance)
 *   - LogisticsService (vehicle distance ranking)
 */
@Service
public class DistanceService {

    private final GoogleMapsService googleMapsService;

    public DistanceService(GoogleMapsService googleMapsService) {
        this.googleMapsService = googleMapsService;
    }

    // -------------------------------------------------------------
    // DISTANCE COMPUTATION (lat/lon -> KM)
    // -------------------------------------------------------------
    public double computeDistance(double lat1, double lng1, double lat2, double lng2) {

        try {
            JSONObject result = googleMapsService.computeRoute(lat1, lng1, lat2, lng2);

            if (result != null && result.has("distance_km")) {
                return result.getDouble("distance_km");
            }

        } catch (Exception e) {
            System.err.println("Google distance lookup failed: " + e.getMessage());
        }

        return haversine(lat1, lng1, lat2, lng2);
    }

    // -------------------------------------------------------------
    // HAVERSINE FALLBACK
    // -------------------------------------------------------------
    private double haversine(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371; // Earth radius in km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
