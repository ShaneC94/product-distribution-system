package com.pds.location.controller;

import com.pds.location.model.Warehouse;
import com.pds.location.repository.WarehouseRepository;
import com.pds.location.service.GoogleMapsService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Handles warehouse operations and integrates Google Maps for distance ranking.
 */
@RestController
@RequestMapping("/api/warehouses")
@CrossOrigin(origins = "*")
public class WarehouseController {

    private final WarehouseRepository warehouseRepository;
    private final GoogleMapsService googleMapsService;

    @Autowired
    public WarehouseController(WarehouseRepository warehouseRepository, GoogleMapsService googleMapsService) {
        this.warehouseRepository = warehouseRepository;
        this.googleMapsService = googleMapsService;
    }

    @GetMapping
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @PostMapping
    public Warehouse addWarehouse(@RequestBody Warehouse warehouse) throws JSONException {
        if (warehouse.getLatitude() == 0 || warehouse.getLongitude() == 0) {
            double[] coords = googleMapsService.geocodeAddress(warehouse.getAddress());
            warehouse.setLatitude(coords[0]);
            warehouse.setLongitude(coords[1]);
        }
        return warehouseRepository.save(warehouse);
    }

    @DeleteMapping("/{id}")
    public String deleteWarehouse(@PathVariable Long id) {
        if (warehouseRepository.existsById(id)) {
            warehouseRepository.deleteById(id);
            return "Warehouse " + id + " deleted.";
        }
        return "Warehouse not found.";
    }

    @GetMapping("/nearest")
    public Warehouse getNearestWarehouse(@RequestParam String address) throws JSONException {
        double[] coords = googleMapsService.geocodeAddress(address);
        List<Warehouse> warehouses = warehouseRepository.findAll();
        if (warehouses.isEmpty()) throw new RuntimeException("No warehouses available.");

        Warehouse nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Warehouse w : warehouses) {
            double distance = haversine(coords[0], coords[1], w.getLatitude(), w.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = w;
            }
        }

        return nearest;
    }

    @GetMapping("/ranked")
    public ResponseEntity<Map<String, Object>> getRankedWarehouses(
            @RequestParam String address,
            @RequestParam(required = false) Integer top) throws JSONException {

        double[] customerCoords = googleMapsService.geocodeAddress(address);
        List<Warehouse> warehouses = warehouseRepository.findAll();
        if (warehouses.isEmpty()) throw new RuntimeException("No warehouses available.");

        List<Map<String, Object>> results = new ArrayList<>();

        for (Warehouse w : warehouses) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", w.getId());
            entry.put("name", w.getName());
            entry.put("address", w.getAddress());

            try {
                JSONObject route = googleMapsService.computeRoute(
                        customerCoords[0], customerCoords[1],
                        w.getLatitude(), w.getLongitude());

                if (route != null && route.has("distance_km")) {
                    entry.put("distance_km", Math.round(route.getDouble("distance_km") * 100.0) / 100.0);
                    entry.put("duration_text", route.optString("duration_text", "N/A"));
                } else {
                    // Fallback to Haversine
                    double fallback = haversine(customerCoords[0], customerCoords[1], w.getLatitude(), w.getLongitude());
                    entry.put("distance_km", Math.round(fallback * 100.0) / 100.0);
                    entry.put("duration_text", "approx");
                }
            } catch (Exception e) {
                // Fallback to Haversine on error
                double fallback = haversine(customerCoords[0], customerCoords[1], w.getLatitude(), w.getLongitude());
                entry.put("distance_km", Math.round(fallback * 100.0) / 100.0);
                entry.put("duration_text", "approx");
            }

            results.add(entry);
        }

        // Sort ascending by distance
        results.sort(Comparator.comparingDouble(e -> (double) e.get("distance_km")));

        if (top != null && top > 0 && top < results.size()) {
            results = results.subList(0, top);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("customer_address", address);
        response.put("results_count", results.size());
        response.put("ranked_warehouses", results);

        return ResponseEntity.ok(response);
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
