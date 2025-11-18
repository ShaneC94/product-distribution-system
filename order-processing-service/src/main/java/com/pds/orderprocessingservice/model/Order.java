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
@Getter
@Setter
@Data // This single annotation handles all getters and setters.
public class Order {

    // Removed the three duplicate field declarations here.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    // The customer's delivery address (KEEP THIS ONE)
    private String deliveryAddress;


    // One-to-Many Relationship: Links the Order to its line items. (KEEP THIS ONE)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING) // (KEEP THIS ONE)
    private OrderStatus status = OrderStatus.RECEIVED;

    private LocalDateTime createdAt = LocalDateTime.now();


    // NOTE: You do not need @Getter, @Setter, and @Data together.
    // @Data includes @Getter, @Setter, @RequiredArgsConstructor, @ToString, and @EqualsAndHashCode.
    // You can safely remove @Getter and @Setter as @Data already provides them.
}