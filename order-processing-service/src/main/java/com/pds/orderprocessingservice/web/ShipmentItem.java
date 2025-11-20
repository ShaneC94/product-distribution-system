package com.pds.orderprocessingservice.web;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentItem {
    private Long productId;
    private int quantity;
    private Long warehouseId;
    // Removed: private String warehouseAddress;

    // Constructors, Getters, Setters...
    public ShipmentItem(Long productId, int quantity, Long warehouseId) { // Constructor updated
        this.productId = productId;
        this.quantity = quantity;
        this.warehouseId = warehouseId;
    }


}