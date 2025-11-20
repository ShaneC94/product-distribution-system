package com.pds.warehouseservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "stock_reservations")
public class StockReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Long warehouseId;

    @Setter
    @Column(name = "product_code", nullable = false)
    private Long productCode;

    @Setter
    private long quantity;

    @Setter
    private Instant createdAt = Instant.now();

    // 1. Default (no-argument) constructor for Hibernate
    public StockReservation() {}


    // 2. Parameterized constructor for service layer instantiation
    public StockReservation(Long warehouseId, Long productCode, long quantity) {
        this.warehouseId = warehouseId;
        this.productCode = productCode;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }
}