package com.pds.faultsreturnsservice.service;

import com.pds.faultsreturnsservice.model.FaultReturn;
import com.pds.faultsreturnsservice.model.FaultReturnStatus;
import com.pds.faultsreturnsservice.repository.FaultReturnRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FaultReturnService {

    private final FaultReturnRepository repository;

    public FaultReturnService(FaultReturnRepository repository) {
        this.repository = repository;
    }

    public FaultReturn create(FaultReturn faultReturn) {
        faultReturn.setStatus(FaultReturnStatus.REPORTED);
        return repository.save(faultReturn);
    }

    public FaultReturn get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fault/Return not found: " + id));
    }

    public List<FaultReturn> listByStatus(FaultReturnStatus status) {
        return repository.findByStatus(status);
    }

    public List<FaultReturn> listByOrderId(Long orderId) {
        return repository.findByOrderId(orderId);
    }

    public FaultReturn markInspected(Long id) {
        FaultReturn entity = get(id);
        entity.setStatus(FaultReturnStatus.INSPECTED);
        return repository.save(entity);
    }

    public FaultReturn markRestocked(Long id) {
        FaultReturn entity = get(id);
        entity.setStatus(FaultReturnStatus.RESTOCKED);
        return repository.save(entity);
    }

    public FaultReturn markDiscarded(Long id) {
        FaultReturn entity = get(id);
        entity.setStatus(FaultReturnStatus.DISCARDED);
        return repository.save(entity);
    }
}
