package com.pds.location.service;

import com.pds.location.model.Warehouse;
import com.pds.location.model.WarehouseDistance;
import com.pds.location.model.WarehouseZoneInfo;
import com.pds.location.repository.WarehouseRepository;

import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class LocationService {

    private final WarehouseRepository warehouseRepository;
    private final GoogleMapsService googleMapsService;

    public LocationService(
            WarehouseRepository warehouseRepository,
            GoogleMapsService googleMapsService
    ) {
        this.warehouseRepository = warehouseRepository;
        this.googleMapsService = googleMapsService;
    }

    // =====================================================================
    // ASYNC DISTANCE COMPUTATION (Routes API with fallback)
    // =====================================================================

    @Async
    public CompletableFuture<WarehouseDistance> computeWarehouseDistanceAsync(
            double[] customer, Warehouse w
    ) {
        CompletableFuture<JSONObject> routeFuture =
                googleMapsService.computeRouteAsync(
                        customer[0], customer[1],
                        w.getLatitude(), w.getLongitude()
                );

        return routeFuture.thenApply(route ->
                buildWarehouseDistance(customer, w, route)
        );
    }

    // =====================================================================
    // Helper used for both async and sync zones/ranking
    // =====================================================================
    private WarehouseDistance buildWarehouseDistance(
            double[] customer, Warehouse w, JSONObject route
    ) {
        double distanceKm;
        long durationSeconds;
        String durationText;

        if (route != null && route.has("distance_km")) {
            distanceKm = round(route.getDouble("distance_km"));
            durationSeconds = route.optLong("duration_seconds", 0);
            durationText = route.optString("duration_text", "unknown");
        } else {
            distanceKm = round(haversine(
                    customer[0], customer[1],
                    w.getLatitude(), w.getLongitude()
            ));
            durationSeconds = 0;
            durationText = "approx";
        }

        return new WarehouseDistance(
                w.getId(),
                w.getName(),
                w.getAddress(),
                distanceKm,
                durationSeconds,
                durationText
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // =====================================================================
    // Haversine fallback
    // =====================================================================
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);

        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // =====================================================================
    // PUBLIC API — ASYNC RANKING WRAPPER (used by controller)
    // =====================================================================
    public List<WarehouseDistance> rankWarehouses(String address) {

        double[] customer = googleMapsService.geocodeAddress(address);
        List<Warehouse> warehouses = warehouseRepository.findAll();

        List<CompletableFuture<WarehouseDistance>> futures =
                warehouses.stream()
                        .map(w -> computeWarehouseDistanceAsync(customer, w))
                        .toList();

        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).join();

        return futures.stream()
                .map(CompletableFuture::join)
                .sorted(Comparator.comparingDouble(WarehouseDistance::getDistanceKm))
                .toList();
    }

    // =====================================================================
    // Zone classification (sync)
    // =====================================================================
    public List<WarehouseZoneInfo> classifyWarehousesByZone(String address) {

        double[] customer = googleMapsService.geocodeAddress(address);

        return warehouseRepository.findAll().stream()
                .map(w -> {
                    JSONObject route = googleMapsService.computeRoute(
                            customer[0], customer[1],
                            w.getLatitude(), w.getLongitude()
                    );

                    WarehouseDistance dist = buildWarehouseDistance(customer, w, route);
                    return buildZoneInfoFromDistance(dist);
                })
                .sorted(Comparator.comparingDouble(WarehouseZoneInfo::getDistanceKm))
                .toList();
    }

    private WarehouseZoneInfo buildZoneInfoFromDistance(WarehouseDistance dist) {
        return new WarehouseZoneInfo(
                dist.getId(),
                dist.getName(),
                dist.getAddress(),
                dist.getDistanceKm(),
                dist.getDurationSeconds(),
                dist.getDurationText(),
                classifyZone(dist.getDistanceKm())
        );
    }

    private String classifyZone(double km) {
        if (km <= 10) return "Zone A (Local)";
        if (km <= 30) return "Zone B (Regional)";
        return "Zone C (Long Distance)";
    }

    // =====================================================================
    // Basic nearest warehouse (fast fallback)
    // =====================================================================
    public Warehouse findNearestWarehouse(double lat, double lon) {
        return warehouseRepository.findAll().stream()
                .min(Comparator.comparingDouble(
                        w -> haversine(lat, lon, w.getLatitude(), w.getLongitude())
                ))
                .orElseThrow(() -> new RuntimeException("No warehouses found"));
    }

    public Warehouse findNearestWarehouseByAddress(String address) {
        double[] coords = googleMapsService.geocodeAddress(address);
        return findNearestWarehouse(coords[0], coords[1]);
    }

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    public String reverseGeocode(double lat, double lon) {
        return googleMapsService.reverseGeocode(lat, lon);
    }


    // -------------------------------------------------------------
    // LOGISTICS ROUTE LOOKUP: Compute distance, duration, and dynamic zone
    // -------------------------------------------------------------
    public WarehouseZoneInfo getWarehouseInfoWithRoute(Long warehouseId, String customerAddress) {

        Warehouse wh = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        double[] customerCoords = googleMapsService.geocodeAddress(customerAddress);

        JSONObject routeJson = googleMapsService.computeRoute(
                wh.getLatitude(), wh.getLongitude(),
                customerCoords[0], customerCoords[1]
        );

        WarehouseDistance dist = buildWarehouseDistance(customerCoords, wh, routeJson);

        String zone = classifyZone(dist.getDistanceKm());

        return new WarehouseZoneInfo(
                dist.getId(),
                dist.getName(),
                dist.getAddress(),
                dist.getDistanceKm(),
                dist.getDurationSeconds(),
                dist.getDurationText(),
                zone
        );
    }

}
