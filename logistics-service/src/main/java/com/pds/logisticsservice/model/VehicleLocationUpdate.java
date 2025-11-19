package com.pds.logisticsservice.model;

/*
    This data is used by vehicles to update their real-time GPS coordinates

    Called from VehicleController -> VehicleService
 */

//TODO: add timestamp of update
public class VehicleLocationUpdate {

    private Long vehicleId;
    private double lat, lng;

    //=====================================================
    // GETTERS AND SETTERS
    //===================================================
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
