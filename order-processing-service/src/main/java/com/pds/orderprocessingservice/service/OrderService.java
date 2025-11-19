package com.pds.orderprocessingservice.service;

import com.pds.orderprocessingservice.model.ItemStatus;
import com.pds.orderprocessingservice.model.Order;
import com.pds.orderprocessingservice.model.OrderItem;
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
        // 1. Save Initial State & Prepare Items (Must ensure items have 'order' set)
        newOrder.setStatus(OrderStatus.RECEIVED);
        newOrder.getItems().forEach(item -> item.setOrder(newOrder)); // Link children to parent
        Order savedOrder = orderRepository.save(newOrder); // Saves items due to CascadeType.ALL

        // 2. Coordinate: Find Potential Warehouses (Ranked by Distance - Priority #1)
        List<Long> candidateWarehouses = findCandidateWarehouses(savedOrder.getDeliveryAddress());

        if (candidateWarehouses.isEmpty()) {
            savedOrder.setStatus(OrderStatus.FAILED);
        } else {
            // 3. Fulfill Items (Checks Stock Availability - Priority #2)
            boolean allItemsReserved = fulfillOrder(savedOrder.getItems(), candidateWarehouses);

            if (allItemsReserved) {
                // Overall status reflects successful reservation
                savedOrder.setStatus(OrderStatus.STOCK_RESERVED);
                // Note: The savedOrder now implicitly knows the assigned warehouses via items list
            } else {
                savedOrder.setStatus(OrderStatus.FAILED);
                // Note: The items list will show which items failed (ItemStatus.NOT_AVAILABLE)
            }
        }

        return orderRepository.save(savedOrder);
    }

    // Helper method to call the Warehouse Service for a single item
    private StockReservationResponse checkAndReserveStock(Long warehouseId, OrderItem item) {
        //specific endpoint
        //calls the http://localhost:8081/reserve-item
        String url = warehouseServiceUrl + "/reserve-item";

        // 1. Construct the payload for the Warehouse Service
        StockReservationRequest payload = new StockReservationRequest(
                warehouseId,
                item.getProductCode(),
                item.getQuantity()
        );

        try {
            // 2. POST the request and expect a structured response (DTO)
            return restTemplate.postForObject(url, payload, StockReservationResponse.class);
        } catch (Exception e) {
            System.err.println("Warehouse " + warehouseId + " call failed for item " + item.getProductCode() + ": " + e.getMessage());
            return new StockReservationResponse(false); // Return failure DTO on exception
        }
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // --- Inter-Service Communication Methods ---

    /**
     * Calls Location Service to get a list of nearby warehouses.
     * Simulates an HTTP call.
     */
    public List<Long> findCandidateWarehouses(String address) {

        // 1. makes a url for the get request
        String url = locationServiceUrl + "/api/warehouses/ranked/async?address=" + address;

        try {
            // Expect a Map where the value is a List, which contains Maps.
            // Use a raw Map here for simplicity
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
     * Calls Warehouse Service to reserve stock atomically.
     * Simulates an HTTP call.
     */

    public List<Order> findAll() {

        return orderRepository.findAll();
    }

    /**
     * Attempts to fulfill all items by checking ranked warehouses in order.
     * @return True if all items were successfully reserved, false otherwise.
     */
    private boolean fulfillOrder(List<OrderItem> items, List<Long> candidateWarehouseIds) {

        for (OrderItem item : items) {

            // 1. Check warehouses one by one for the current item
            boolean itemReserved = false;

            for (Long warehouseId : candidateWarehouseIds) {

                // 2. uses the internal method checkAndReserveStock to call warehouse, stores response
                StockReservationResponse response = checkAndReserveStock(warehouseId, item);

                if (response != null && response.isSuccess()) {
                    // SUCCESS: Stock found and reserved at this warehouse
                    item.setFulfilledByWarehouseId(warehouseId);
                    item.setItemStatus(ItemStatus.RESERVED);
                    itemReserved = true;
                    break; // Move to the next item in the order
                }
            }

            // 3. If the item couldn't be reserved at ANY candidate warehouse
            if (!itemReserved) {
                item.setItemStatus(ItemStatus.NOT_AVAILABLE);
                // In a real system, this would trigger backorder or customer notification
                // We return false, causing the entire order to fail for simplicity.
                return false;
            }
        }
        // All items were successfully reserved across the candidate warehouses
        return true;
    }
}

