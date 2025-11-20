package com.pds.location.controller;

import com.pds.location.model.Warehouse;
import com.pds.location.service.LocationService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * REST API providing geolocation utilities and diagnostic tools.
 * Endpoints Include:
 *   - Nearest warehouse by lat/lon or address
 *   - Reverse geocoding (lat/lon → address)
 *   - Cache inspection (/location/debug)
 *   - Clear all caches (/location/clearCache)
 * Used For:
 *   - Internal testing and debugging
 *   - Verifying geocoding and routing behavior
 *   - Inspecting cache contents at runtime
 * Integration:
 *   - LocationService (warehouse logic)
 *   - CacheManager (Spring caching)
 */

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private CacheManager cacheManager;

    // -------------------------------------------------------------
    // NEAREST WAREHOUSE (using raw latitude/longitude input)
    // -------------------------------------------------------------
    @GetMapping("/nearest")
    public Warehouse getNearestWarehouse(@RequestParam double lat, @RequestParam double lon) {
        return locationService.findNearestWarehouse(lat, lon);
    }

    // -------------------------------------------------------------
    // NEAREST WAREHOUSE (geocoding the address first)
    // -------------------------------------------------------------
    @GetMapping("/nearestByAddress")
    public Warehouse getNearestWarehouseByAddress(@RequestParam String address) throws JSONException {
        return locationService.findNearestWarehouseByAddress(address);
    }

    // -------------------------------------------------------------
    // DEBUG: View warehouses and all cached entries
    // -------------------------------------------------------------
    @GetMapping("/debug")
    public Map<String, Object> debugInfo() {
        Map<String, Object> result = new HashMap<>();
        result.put("warehouses", locationService.getAllWarehouses());
        result.put("cached_geocodes", extractCacheEntries("geocodeCache"));
        result.put("cached_distances", extractCacheEntries("distanceCache"));
        return result;
    }

    // -------------------------------------------------------------
    // CLEAR ALL CACHES (geocode + distance)
    // -------------------------------------------------------------
    @GetMapping("/clearCache")
    public String clearCaches() {
        cacheManager.getCacheNames().forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
        return "All caches cleared.";
    }

    // -------------------------------------------------------------
    // Helper: Extract entries from a named cache
    // -------------------------------------------------------------
    private Map<Object, Object> extractCacheEntries(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) return Map.of("status", "not found");

        Map<Object, Object> entries = new LinkedHashMap<>();
        try {
            Object nativeCache = cache.getNativeCache();
            if (nativeCache instanceof Map<?, ?> map) {
                entries.putAll(map);
            } else {
                entries.put("info", "Cache type not iterable: " + nativeCache.getClass().getSimpleName());
            }
        } catch (Exception e) {
            entries.put("error", e.getMessage());
        }
        return entries;
    }

    // -------------------------------------------------------------
    // REVERSE GEOCODING: Convert lat/lon back into an address
    // -------------------------------------------------------------
    @GetMapping("/reverseGeocode")
    public String reverseGeocode(@RequestParam double lat, @RequestParam double lon) {
        return locationService.reverseGeocode(lat, lon);
    }




}
