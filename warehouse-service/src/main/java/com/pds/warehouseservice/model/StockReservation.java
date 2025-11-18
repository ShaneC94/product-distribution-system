package com.pds.warehouseservice.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "stock_reservations")
public class StockReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private long quantity;

    private Instant createdAt = Instant.now();

    public StockReservation() {}
    public StockReservation(Long warehouseId, Product product, long quantity) {
        this.warehouseId = warehouseId;
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public long getQuantity() { return quantity; }
    public void setQuantity(long quantity) { this.quantity = quantity; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
