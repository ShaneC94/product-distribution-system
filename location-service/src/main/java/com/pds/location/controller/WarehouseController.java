package com.pds.location.controller;

import com.pds.location.model.Warehouse;
import com.pds.location.model.WarehouseDistance;
import com.pds.location.model.WarehouseZoneInfo;
import com.pds.location.repository.WarehouseRepository;
import com.pds.location.service.GoogleMapsService;
import com.pds.location.service.LocationService;

import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST API for managing warehouses and computing routes/zones.
 * Endpoints Include:
 *   - CRUD operations for warehouses
 *   - Rank warehouses by travel distance (async)
 *   - Classify warehouses into zones (A/B/C)
 *   - Compute route details to a customer (distance + duration + zone)
 *   - Nearest warehouse lookup
 * Integration:
 *   - GoogleMapsService for geocoding and routing
 *   - LocationService for ranking and classification logic
 *   - WarehouseRepository for DB operations
 */

@RestController
@RequestMapping("/api/warehouses")
@CrossOrigin(origins = "*")
public class WarehouseController {

    private final WarehouseRepository warehouseRepository;
    private final GoogleMapsService googleMapsService;
    private final LocationService locationService;

    public WarehouseController(
            WarehouseRepository warehouseRepository,
            GoogleMapsService googleMapsService,
            LocationService locationService
    ) {
        this.warehouseRepository = warehouseRepository;
        this.googleMapsService = googleMapsService;
        this.locationService = locationService;
    }

    // -------------------------------------------------------------
    // CRUD: Fetch all warehouses
    // -------------------------------------------------------------
    @GetMapping
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    // -------------------------------------------------------------
    // CRUD: Add warehouse, automatically geocode if needed
    // -------------------------------------------------------------
    @PostMapping
    public Warehouse addWarehouse(@RequestBody Warehouse warehouse) throws JSONException {
        if (warehouse.getLatitude() == 0 || warehouse.getLongitude() == 0) {
            double[] coords = googleMapsService.geocodeAddress(warehouse.getAddress());
            warehouse.setLatitude(coords[0]);
            warehouse.setLongitude(coords[1]);
        }
        return warehouseRepository.save(warehouse);
    }

    // -------------------------------------------------------------
    // CRUD: Delete warehouse
    // -------------------------------------------------------------
    @DeleteMapping("/{id}")
    public String deleteWarehouse(@PathVariable Long id) {
        if (warehouseRepository.existsById(id)) {
            warehouseRepository.deleteById(id);
            return "Warehouse " + id + " deleted.";
        }
        return "Warehouse not found.";
    }

    // -------------------------------------------------------------
    // Nearest warehouse (fast haversine fallback)
    // -------------------------------------------------------------
    @GetMapping("/nearest")
    public Warehouse getNearestWarehouse(@RequestParam String address) throws JSONException {
        double[] customerCoords = googleMapsService.geocodeAddress(address);

        return warehouseRepository.findAll().stream()
                .min(Comparator.comparingDouble(w ->
                        haversine(customerCoords[0], customerCoords[1],
                                w.getLatitude(), w.getLongitude())))
                .orElseThrow(() -> new RuntimeException("No warehouses found"));
    }

    // -------------------------------------------------------------
    // ASYNC RANKED WAREHOUSES (Google Routes + fallback)
    // -------------------------------------------------------------
    @GetMapping("/ranked/async")
    public ResponseEntity<Map<String, Object>> getRankedWarehousesAsync(
            @RequestParam String address
    ) {

        // sync wrapper around the async method
        List<WarehouseDistance> ranked = locationService.rankWarehouses(address);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("customer_address", address);
        response.put("results_count", ranked.size());
        response.put("ranked_warehouses", ranked);

        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------
    // CLASSIFY WAREHOUSES INTO ZONES A/B/C
    // -------------------------------------------------------------
    @GetMapping("/zones")
    public ResponseEntity<Map<String, Object>> getZones(@RequestParam String address) {
        List<WarehouseZoneInfo> zoneList = locationService.classifyWarehousesByZone(address);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("customer_address", address);
        response.put("results_count", zoneList.size());
        response.put("warehouses", zoneList);

        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------
    // Helper method: Haversine distance
    // -------------------------------------------------------------
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // -------------------------------------------------------------
    // ROUTE LOOKUP: Compute distance, duration, and dynamic zone
    // -------------------------------------------------------------
    @GetMapping("/{id}/route")
    public ResponseEntity<WarehouseZoneInfo> getWarehouseRouteToCustomer(
            @PathVariable Long id,
            @RequestParam String customerAddress
    ) throws JSONException {

        WarehouseZoneInfo info = locationService.getWarehouseInfoWithRoute(id, customerAddress);
        return ResponseEntity.ok(info);
    }

}
