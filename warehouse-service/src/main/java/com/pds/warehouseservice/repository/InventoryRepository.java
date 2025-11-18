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
    @Query("select i from Inventory i where i.warehouseId = :warehouseId and i.product.productCode = :productCode")
    Optional<Inventory> findByWarehouseAndProductForUpdate(@Param("warehouseId") Long warehouseId, @Param("productCode") String productCode);

    Optional<Inventory> findByWarehouseIdAndProduct_ProductCode(Long warehouseId, String productCode);
}
