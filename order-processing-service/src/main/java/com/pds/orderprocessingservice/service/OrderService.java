package com.pds.orderprocessingservice.service;

import com.pds.orderprocessingservice.model.Order;
import com.pds.orderprocessingservice.model.OrderStatus;
import com.pds.orderprocessingservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Value("${service.location.url}")
    private String locationServiceUrl;

    @Value("${service.warehouse.url}")
    private String warehouseServiceUrl;

    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * 1. Saves the initial order.
     * 2. Coordinates with Location Service for potential warehouses.
     * 3. Applies load balancing to choose the best warehouse.
     * 4. Coordinates with Warehouse Service to reserve stock.
     */
    @Transactional
    public Order processNewOrder(Order newOrder) {
        // 1. Save Initial State
        newOrder.setStatus(OrderStatus.RECEIVED);
        Order savedOrder = orderRepository.save(newOrder);

        // 2. Coordinate: Find Potential Warehouses (Placeholder for DTO)
        List<Long> candidateWarehouses = findCandidateWarehouses(savedOrder.getDeliveryAddress());

        if (candidateWarehouses.isEmpty()) {
            savedOrder.setStatus(OrderStatus.FAILED);
            return orderRepository.save(savedOrder);
        }

        // 3. Coordinate: Load Balancing and Assignment
        Long assignedWarehouseId = selectBestWarehouse(candidateWarehouses);
        savedOrder.setAssignedWarehouseId(assignedWarehouseId);
        savedOrder.setStatus(OrderStatus.ASSIGNED);
        savedOrder = orderRepository.save(savedOrder);

        // 4. Coordinate: Reserve Stock (Simplified synchronous call)
        boolean stockReserved = reserveStock(savedOrder.getId(), assignedWarehouseId);

        if (stockReserved) {
            savedOrder.setStatus(OrderStatus.STOCK_RESERVED);
            // 5. COORDINATE: Publish to Logistics Service for Scheduling (TODO)
        } else {
            savedOrder.setStatus(OrderStatus.FAILED);
            // In a real system, you would handle re-assignment or customer notification
        }

        return orderRepository.save(savedOrder);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // --- Inter-Service Communication Methods ---

    /**
     * Calls Location Service to get a list of nearby warehouses.
     * Simulates an HTTP call.
     */
    private List<Long> findCandidateWarehouses(String address) {

        // 1. makes a url for the get request
        String url = locationServiceUrl + "/api/warehouses/ranked/async?address=" + address;

        try {
            // Use generics to tell RestTemplate exactly what to expect.
            // We expect a Map where the value is a List, which contains Maps.
            // We'll use a raw Map here for simplicity, but acknowledge the unchecked cast.
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // --- Response Parsing and Extraction ---
            if (response != null && response.containsKey("ranked_warehouses")) {
                // Unchecked cast is necessary here since RestTemplate returns a raw List
                List<Map<String, Object>> rankedWarehouses = (List<Map<String, Object>>) response.get("ranked_warehouses");

                // 3. Extract only the IDs from the ranked list
                // Use Integer.toLong() instead of longValue() for clarity (since JSON numbers are usually Integer)
                return rankedWarehouses.stream()
                        .map(warehouseInfo -> ((Integer) warehouseInfo.get("id")).longValue())
                        .toList();
            }
            return List.of();

        } catch (Exception e) {
            System.err.println("Error calling Location Service: " + e.getMessage());
            // Placeholder for local testing - ensure you remove this for production readiness
            return List.of(101L, 102L);
        }
    }

    /**
     * Implements the Load Balancing strategy (e.g., Least Capacity Used).
     * Simulates fetching load data and choosing the best one.
     */
    private Long selectBestWarehouse(List<Long> candidateIds) {
        // Placeholder for a real Load Balancing implementation.
        // In reality, this would involve a second call to a Warehouse Status API
        // to fetch metrics (e.g., remaining capacity or current load).
        // For now, we'll just pick the first one.
        System.out.println("Applying load balancing among: " + candidateIds);
        return candidateIds.get(0);
    }

    /**
     * Calls Warehouse Service to reserve stock atomically.
     * Simulates an HTTP call.
     */
    private boolean reserveStock(Long orderId, Long warehouseId) {
        // Example: POST http://localhost:8082/api/warehouse/reserve-stock
        String url = warehouseServiceUrl + "/reserve-stock";
        // Order details (products/quantities) would be included in the request body (DTO).
        try {
            restTemplate.postForLocation(url, new Object()); // Placeholder request
            System.out.println("Stock reservation requested for Order " + orderId + " at Warehouse " + warehouseId);
            return true; // Assume success if no exception
        } catch (Exception e) {
            System.err.println("Stock reservation failed: " + e.getMessage());
            return false;
        }
    }

    public List<Order> findAll() {

        return orderRepository.findAll();
    }
}