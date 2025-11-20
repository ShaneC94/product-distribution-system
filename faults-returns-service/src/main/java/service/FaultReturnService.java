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

    public FaultReturn reportFault(FaultReturn faultReturn) {
        faultReturn.setStatus(FaultReturnStatus.REPORTED);
        return repository.save(faultReturn);
    }

    public List<FaultReturn> getAll() {
        return repository.findAll();
    }

    public FaultReturn updateStatus(Long id, FaultReturnStatus status) {
        FaultReturn faultReturn = repository.findById(id).orElseThrow();
        faultReturn.setStatus(status);
        return repository.save(faultReturn);
    }
}