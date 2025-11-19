package com.pds.orderprocessingservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "order_item")
@Data
public class OrderItem {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productCode;
    private int quantity;

    // Which warehouse reserved this specific item (Split Shipment Tracking)
    private Long fulfilledByWarehouseId;

    // Status of the reservation for this item
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus = ItemStatus.PENDING;

    // Many-to-One Relationship: Links the item back to the parent Order (the owning side).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;
}