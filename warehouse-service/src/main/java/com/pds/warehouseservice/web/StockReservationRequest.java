package com.pds.warehouseservice.web;

public class StockReservationRequest {
    private Long warehouseId;
    private String productCode;
    private int quantity;

    public StockReservationRequest() {}

    public StockReservationRequest(Long warehouseId, String productCode, int quantity) {
        this.warehouseId = warehouseId;
        this.productCode = productCode;
        this.quantity = quantity;
    }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
