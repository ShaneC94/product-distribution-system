package com.pds.faultsreturnsservice.repository;

import com.pds.faultsreturnsservice.model.FaultReturn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaultReturnRepository extends JpaRepository<FaultReturn, Long> {
}