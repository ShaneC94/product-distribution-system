package com.pds.orderprocessingservice.service;

// DTO used for the POST request body
public class StockReservationRequest {

    private Long warehouseId;
    private String productCode;
    private int quantity;

    /**
     * All-args constructor to create the payload before sending via RestTemplate.
     */
    public StockReservationRequest(Long warehouseId, String productCode, int quantity) {
        this.warehouseId = warehouseId;
        this.productCode = productCode;
        this.quantity = quantity;
    }

    // --- Getters (Required by Spring's Jackson for serialization into JSON) ---

    public Long getWarehouseId() {
        return warehouseId;
    }

    public String getProductCode() {
        return productCode;
    }

    public int getQuantity() {
        return quantity;
    }


}