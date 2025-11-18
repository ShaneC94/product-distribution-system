package com.pds.orderprocessingservice.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DTO used for the POST response body
@Getter // Provides getters for all fields
@Setter // Provides setters for all fields (useful if Jackson needs them)
@NoArgsConstructor // Required by Jackson for deserialization from JSON
public class StockReservationResponse {

    // Core success flag. Lombok creates boolean methods like isSuccess()
    private boolean success;

    // Identifier for the successful reservation
    private Long reservationId;

    /**
     * Constructor specifically for the failure case (used in catch blocks).
     */
    public StockReservationResponse(boolean success) {
        this.success = success;
        // reservationId remains null if success is false
    }

    /**
     * Optional: All-args constructor for the success case.
     */
    public StockReservationResponse(boolean success, Long reservationId) {
        this.success = success;
        this.reservationId = reservationId;
    }
}