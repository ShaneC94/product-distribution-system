package com.pds.orderprocessingservice.web;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentItem {
    private Long productId;
    private int quantity;
    private Long warehouseId;


    // Constructors
    public ShipmentItem(Long productId, int quantity, Long warehouseId) {
        this.productId = productId;
        this.quantity = quantity;
        this.warehouseId = warehouseId;
    }


}