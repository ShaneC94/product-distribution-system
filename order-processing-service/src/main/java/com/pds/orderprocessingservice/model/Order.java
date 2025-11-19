package com.pds.orderprocessingservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "customer_order")
 // @Data includes @Getter, @Setter, @RequiredArgsConstructor, @ToString, and @EqualsAndHashCode.
@Data
public class Order {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    // The customer's delivery address
    private String deliveryAddress;


    // One-to-Many Relationship: Links the Order to its line items.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.RECEIVED;

    private LocalDateTime createdAt = LocalDateTime.now();


    }