package com.pds.orderprocessingservice.repository;

import com.pds.orderprocessingservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

// Provides CRUD methods automatically (save, findById, findAll, etc.)
public interface OrderRepository extends JpaRepository<Order, Long> {
}