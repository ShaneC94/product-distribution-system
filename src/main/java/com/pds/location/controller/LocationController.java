package com.pds.location.controller;

import com.pds.location.model.Warehouse;
import com.pds.location.service.LocationService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/nearest")
    public Warehouse getNearestWarehouse(@RequestParam double lat, @RequestParam double lon) {
        return locationService.findNearestWarehouse(lat, lon);
    }

    @GetMapping("/nearestByAddress")
    public Warehouse getNearestWarehouseByAddress(@RequestParam String address) throws JSONException {
        return locationService.findNearestWarehouseByAddress(address);
    }

    @GetMapping("/debug")
    public Map<String, Object> debugInfo() {
        Map<String, Object> result = new HashMap<>();
        result.put("warehouses", locationService.getAllWarehouses());
        result.put("cached_geocodes", extractCacheEntries("geocodeCache"));
        result.put("cached_distances", extractCacheEntries("distanceCache"));
        return result;
    }

    @GetMapping("/clearCache")
    public String clearCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            Objects.requireNonNull(cacheManager.getCache(name)).clear();
        });
        return "All caches cleared.";
    }

    private Map<Object, Object> extractCacheEntries(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) return Map.of("status", "not found");

        Map<Object, Object> entries = new LinkedHashMap<>();
        try {
            Object nativeCache = cache.getNativeCache();
            if (nativeCache instanceof Map<?, ?> map) {
                map.forEach((k, v) -> entries.put(k, v));
            } else {
                entries.put("info", "Cache type not iterable: " + nativeCache.getClass().getSimpleName());
            }
        } catch (Exception e) {
            entries.put("error", e.getMessage());
        }
        return entries;
    }
}
