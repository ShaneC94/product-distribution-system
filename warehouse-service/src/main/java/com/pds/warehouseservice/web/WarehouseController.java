package com.pds.warehouseservice.web;

import com.pds.warehouseservice.service.InventoryService;
import com.pds.warehouseservice.web.StockReservationRequest;
import com.pds.warehouseservice.web.StockReservationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WarehouseController {

    private final InventoryService inventoryService;

    public WarehouseController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Matches the endpoint expected by OrderProcessingService:
     * POST {warehouseServiceUrl}/reserve-item
     *
     * Accepts JSON body and returns StockReservationResponse.
     */
    @PostMapping("/reserve-item")
    public ResponseEntity<StockReservationResponse> reserveItem(@RequestBody StockReservationRequest request) {
        StockReservationResponse resp = inventoryService.reserveItem(request);
        if (resp.isSuccess()) {
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.status(409).body(resp); // conflict / failure
        }
    }
}
