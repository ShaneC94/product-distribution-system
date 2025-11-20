package com.pds.orderprocessingservice.model;

public enum ItemStatus {
    PENDING,
    RESERVED,
    NOT_AVAILABLE,
    SPLIT_SHIPMENT // Optional: if item must be shipped from a secondary location
}