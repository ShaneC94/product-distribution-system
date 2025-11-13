package com.pds.location.service;

import com.pds.location.model.GeocodeCache;
import com.pds.location.repository.GeocodeCacheRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * Provides geocoding and routing functionality via Google Maps APIs.
 * Uses the modern Google Routes API (Distance Matrix v2).
 * Includes persistent MySQL caching and distance calculation.
 * Removed fallback logic to ensure only precise address results are used.
 */
@Service
public class GoogleMapsService {

    private final GeocodeCacheRepository geocodeCacheRepository;
    private final RestTemplate restTemplate;
    private final String apiKey;

    @Autowired
    public GoogleMapsService(
            GeocodeCacheRepository geocodeCacheRepository,
            @Value("${google.maps.api.key}") String apiKey
    ) {
        this.geocodeCacheRepository = geocodeCacheRepository;
        this.apiKey = apiKey;

        this.restTemplate = new RestTemplate();
        this.restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory());
    }

    /**
     * Geocode an address into latitude/longitude using Google Maps Geocoding API.
     * Uses MySQL cache before calling external APIs.
     */
    @Cacheable("geocodeCache")
    public double[] geocodeAddress(String address) {
        final String normalizedAddress = normalizeAddress(address);

        // Check MySQL cache first and update last_accessed if found
        return geocodeCacheRepository.findByNormalizedAddress(normalizedAddress)
                .map(cache -> {
                    // Update last accessed timestamp
                    geocodeCacheRepository.touchLastAccess(normalizedAddress);
                    return new double[]{cache.getLatitude(), cache.getLongitude()};
                })
                .orElseGet(() -> {
                    double[] coords = fetchCoordinates(normalizedAddress);
                    if (coords != null) {
                        geocodeCacheRepository.save(new GeocodeCache(normalizedAddress, coords[0], coords[1]));
                        return coords;
                    }

                    System.err.println("Geocoding failed for: " + normalizedAddress);
                    return null;
                });
    }

    /**
     * Normalize and append country if missing.
     */
    private String normalizeAddress(String address) {
        String normalized = address.trim();
        if (!normalized.toLowerCase().contains("canada")) {
            normalized += ", Canada";
        }
        return normalized;
    }

    /**
     * Makes one geocoding request to the Google Maps Geocoding API.
     */
    private double[] fetchCoordinates(String address) {
        try {
            String url = String.format(
                    "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",
                    java.net.URLEncoder.encode(address, java.nio.charset.StandardCharsets.UTF_8),
                    apiKey
            );

            String response = restTemplate.getForObject(url, String.class);
            if (response == null) return null;

            JSONObject json = new JSONObject(response);

            if (!"OK".equals(json.optString("status"))) {
                System.err.println("Geocoding failed for: " + address + " | Status: " + json.optString("status"));
                return null;
            }

            JSONObject location = json.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location");

            return new double[]{location.getDouble("lat"), location.getDouble("lng")};
        } catch (Exception e) {
            System.err.println("Error geocoding: " + e.getMessage());
            return null;
        }
    }

    /**
     * Compute driving distance and duration between two coordinates using Google Routes API v2.
     * Uses only the new format ("duration": "123s").
     * Returns distance (km) and duration (seconds).
     */
    @Cacheable("distanceCache")
    public JSONObject computeRoute(double fromLat, double fromLon, double toLat, double toLon) {
        try {
            String url = "https://routes.googleapis.com/distanceMatrix/v2:computeRouteMatrix";

            JSONObject requestBody = new JSONObject()
                    .put("origins", new JSONArray().put(new JSONObject()
                            .put("waypoint", new JSONObject()
                                    .put("location", new JSONObject()
                                            .put("latLng", new JSONObject()
                                                    .put("latitude", fromLat)
                                                    .put("longitude", fromLon))))))
                    .put("destinations", new JSONArray().put(new JSONObject()
                            .put("waypoint", new JSONObject()
                                    .put("location", new JSONObject()
                                            .put("latLng", new JSONObject()
                                                    .put("latitude", toLat)
                                                    .put("longitude", toLon))))))
                    .put("travelMode", "DRIVE");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Goog-Api-Key", apiKey);
            headers.set("X-Goog-FieldMask", "originIndex,destinationIndex,duration,distanceMeters,status");

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(requestBody.toString(), headers), String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println("Routes API failed: " + response.getStatusCode());
                return null;
            }

            JSONArray routes = new JSONArray(response.getBody());
            if (routes.isEmpty()) return null;

            JSONObject route = routes.getJSONObject(0);
            if (!route.has("distanceMeters") || !route.has("duration")) {
                System.err.println("Routes API returned incomplete data: " + route.optString("status", "UNKNOWN"));
                return null;
            }

            double distanceMeters = route.optDouble("distanceMeters", 0);

            // Parse new format duration string, e.g. "123s"
            String durationStr = route.optString("duration", "0s").replace("s", "");
            long durationSeconds = 0;
            try {
                durationSeconds = Long.parseLong(durationStr);
            } catch (NumberFormatException ignored) {}

            JSONObject result = new JSONObject();
            result.put("distance_km", distanceMeters / 1000.0);
            result.put("duration_seconds", durationSeconds);
            result.put("duration_text",
                    durationSeconds > 0 ? (durationSeconds / 60) + " min" : "unknown");

            return result;

        } catch (Exception e) {
            System.err.println("Error in computeRoute: " + e.getMessage());
            return null;
        }
    }
}
