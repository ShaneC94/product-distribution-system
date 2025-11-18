package com.pds.warehouseservice.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "inventories",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"warehouse_id", "product_id"})})
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private long availableQuantity;

    @Column(nullable = false)
    private long reservedQuantity;

    private Instant updatedAt;

    public Inventory() {}

    public Inventory(Long warehouseId, Product product, long availableQuantity) {
        this.warehouseId = warehouseId;
        this.product = product;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = 0;
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public long getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(long availableQuantity) { this.availableQuantity = availableQuantity; }
    public long getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(long reservedQuantity) { this.reservedQuantity = reservedQuantity; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public void touch() { this.updatedAt = Instant.now(); }
}
