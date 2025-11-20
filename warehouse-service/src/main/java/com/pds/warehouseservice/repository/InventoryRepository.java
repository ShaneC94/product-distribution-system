package com.pds.warehouseservice.repository;

import com.pds.warehouseservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.warehouseId = :warehouseId AND i.product.productCode = :productCode")
    Optional<Inventory> findByWarehouseAndProductForUpdate(Long warehouseId, Long productCode);}