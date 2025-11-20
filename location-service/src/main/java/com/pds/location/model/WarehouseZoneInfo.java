package com.pds.location.model;

/**
 * Represents a warehouse enriched with route and zone classification metadata.
 * Contains:
 *   - Warehouse identity (id, name, address)
 *   - Driving distance (km)
 *   - Driving duration (seconds & readable text)
 *   - Dynamic delivery zone assignment (A/B/C)
 * Produced By:
 *   - LocationService.classifyWarehousesByZone()
 *   - LocationService.getWarehouseInfoWithRoute()
 * Returned In:
 *   - /api/warehouses/zones
 *   - /api/warehouses/{id}/route
 */

public class WarehouseZoneInfo {
    private Long id;
    private String name;
    private String address;
    private double distanceKm;
    private long durationSeconds;
    private String durationText;
    private String zone;

    public WarehouseZoneInfo(Long id, String name, String address,
                             double distanceKm, long durationSeconds, String durationText,
                             String zone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.distanceKm = distanceKm;
        this.durationSeconds = durationSeconds;
        this.durationText = durationText;
        this.zone = zone;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getDistanceKm() { return distanceKm; }
    public long getDurationSeconds() { return durationSeconds; }
    public String getDurationText() { return durationText; }
    public String getZone() { return zone; }
}
