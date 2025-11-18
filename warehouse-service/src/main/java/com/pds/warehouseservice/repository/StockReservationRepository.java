package com.pds.warehouseservice.repository;

import com.pds.warehouseservice.model.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {
}
