package com.pds.warehouseservice.web.dto;

public class InventoryDto {
    private Long warehouseId;
    private Long productId;
    private String productName;
    private long availableQuantity;
    private long reservedQuantity;

    // getters and setters
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public long getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(long availableQuantity) { this.availableQuantity = availableQuantity; }
    public long getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(long reservedQuantity) { this.reservedQuantity = reservedQuantity; }
}

