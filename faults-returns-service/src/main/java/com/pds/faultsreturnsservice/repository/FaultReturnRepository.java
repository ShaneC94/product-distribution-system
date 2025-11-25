package com.pds.faultsreturnsservice.repository;

import com.pds.faultsreturnsservice.model.FaultReturn;
import com.pds.faultsreturnsservice.model.FaultReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FaultReturnRepository extends JpaRepository<FaultReturn, Long> {

    List<FaultReturn> findByStatus(FaultReturnStatus status);

    List<FaultReturn> findByOrderId(Long orderId);
}
