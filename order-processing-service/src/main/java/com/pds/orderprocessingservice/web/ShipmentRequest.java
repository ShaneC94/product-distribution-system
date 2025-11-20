package com.pds.orderprocessingservice.web;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
    public class ShipmentRequest {
        private Long orderId;
        private String customerDeliveryAddress;
        private List<ShipmentItem> items;

    public ShipmentRequest(Long id, String deliveryAddress, List<ShipmentItem> shipmentItems) {
    }

}

