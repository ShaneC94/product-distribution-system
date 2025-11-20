package com.pds.location.model;

/**
 * Represents computed travel distance & duration between a customer and a warehouse.
 * Contains:
 *   - Warehouse identity (id, name, address)
 *   - Distance in kilometers
 *   - Duration in seconds
 *   - Readable duration text ("12 min")
 * Used Internally By:
 *   - LocationService for ranking and zone classification
 *   - Returned in /api/warehouses/ranked/async
 */

public class WarehouseDistance {
    private Long id;
    private String name;
    private String address;
    private double distanceKm;
    private long durationSeconds;
    private String durationText;

    public WarehouseDistance(Long id, String name, String address,
                             double distanceKm, long durationSeconds, String durationText) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.distanceKm = distanceKm;
        this.durationSeconds = durationSeconds;
        this.durationText = durationText;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getDistanceKm() { return distanceKm; }
    public long getDurationSeconds() { return durationSeconds; }
    public String getDurationText() { return durationText; }
}
