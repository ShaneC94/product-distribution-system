package com.pds.logisticsservice.model;

/*
    Data is sent from order-processing-service to logistics-service during vehicle assignment

    This represents the fields to select a vehicle, not the full order
*/

//TODO: add delivery priority (LOW, NORMAL, HIGH)
//TODO: add drop-off locations

public class OrderRequest {
    private Long orderId; // ID of incoming order
    private double pickupLat, pickupLng;
    private double volume; // Volume/weight of the order

    //==============================================
    // Constructors required
    //=============================================

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
}
