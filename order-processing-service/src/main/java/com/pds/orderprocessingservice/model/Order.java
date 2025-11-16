package com.pds.orderprocessingservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private String deliveryAddress;

    // The key piece of data for assignment
    private Long assignedWarehouseId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.RECEIVED;

    private LocalDateTime createdAt = LocalDateTime.now();

    // NOTE: OrderItem details are omitted for brevity,
    // but should be mapped here (e.g., using a @OneToMany list).

    // Getters and Setters (omitted for brevity)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ... other getters/setters

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public Long getAssignedWarehouseId() { return assignedWarehouseId; }
    public void setAssignedWarehouseId(Long assignedWarehouseId) { this.assignedWarehouseId = assignedWarehouseId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}