package com.pds.warehouseservice.service;

import com.pds.warehouseservice.model.Inventory;
import com.pds.warehouseservice.model.Product;
import com.pds.warehouseservice.model.StockReservation;
import com.pds.warehouseservice.repository.InventoryRepository;
import com.pds.warehouseservice.repository.ProductRepository;
import com.pds.warehouseservice.repository.StockReservationRepository;
import com.pds.warehouseservice.web.StockReservationRequest;
import com.pds.warehouseservice.web.StockReservationResponse;
import com.pds.warehouseservice.web.exception.InsufficientStockException;
import com.pds.warehouseservice.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final StockReservationRepository reservationRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            ProductRepository productRepository,
                            StockReservationRepository reservationRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Atomically reserve stock in a particular warehouse.
     * Returns a StockReservationResponse with success + reservationId on success.
     */
    @Transactional
    public StockReservationResponse reserveItem(StockReservationRequest req) {
        if (req.getQuantity() <= 0) {
            return new StockReservationResponse(false);
        }

        // 1) find product
        Product product = productRepository.findByProductCode(req.getProductCode())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + req.getProductCode()));

        // 2) lock inventory row
        Inventory inv = inventoryRepository.findByWarehouseAndProductForUpdate(req.getWarehouseId(), req.getProductCode())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for warehouse " + req.getWarehouseId() + " product " + req.getProductCode()));

        // 3) check availability
        if (inv.getAvailableQuantity() < req.getQuantity()) {
            throw new InsufficientStockException("Not enough stock. Available=" + inv.getAvailableQuantity() + " requested=" + req.getQuantity());
        }

        // 4) adjust counts
        inv.setAvailableQuantity(inv.getAvailableQuantity() - req.getQuantity());
        inv.setReservedQuantity(inv.getReservedQuantity() + req.getQuantity());
        inv.touch();
        inventoryRepository.save(inv);

        // 5) create reservation record
        StockReservation reservation = new StockReservation(req.getWarehouseId(), product, req.getQuantity());
        StockReservation saved = reservationRepository.save(reservation);

        return new StockReservationResponse(true, saved.getId());
    }
}
