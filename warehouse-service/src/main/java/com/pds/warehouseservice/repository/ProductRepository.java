package com.pds.warehouseservice.repository;

import com.pds.warehouseservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductCode(Long productCode);
}
