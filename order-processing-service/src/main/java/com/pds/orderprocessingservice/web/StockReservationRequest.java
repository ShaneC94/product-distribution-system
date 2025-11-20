package com.pds.orderprocessingservice.web;

import lombok.Getter;

// DTO used for the POST request body
@Getter
public class StockReservationRequest {

    private final Long warehouseId;
    private final Long productCode;
    private final int quantity;

    /**
     * All-args constructor to create the payload before sending via RestTemplate.
     */
    public StockReservationRequest(Long warehouseId, Long productCode, int quantity) {
        this.warehouseId = warehouseId;
        this.productCode = productCode;
        this.quantity = quantity;
    }




}