package com.pds.orderprocessingservice.model;

public enum OrderStatus {
    RECEIVED,
    ASSIGNED,
    STOCK_RESERVED,
    SCHEDULED_FOR_DELIVERY,
    DELIVERED,
    FAILED // For cases like stock reservation failure
}