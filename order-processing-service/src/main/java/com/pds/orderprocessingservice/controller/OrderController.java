package com.pds.orderprocessingservice.controller;

import com.pds.orderprocessingservice.model.Order;
import com.pds.orderprocessingservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping // Maps to GET /api/orders
    public ResponseEntity<List<Order>> getAllOrders() {

        List<Order> orders = this.orderService.findAll();
        if (orders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        // The service handles the entire workflow: assignment, stock reservation, and state update
        Order processedOrder = orderService.processNewOrder(order);
        return new ResponseEntity<>(processedOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(order -> new ResponseEntity<>(order, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Additional endpoints for status updates (e.g., from Logistics)
    // PATCH /api/orders/{id}/status/DELIVERED
}