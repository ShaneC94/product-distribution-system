package com.pds.faultsreturnsservice.controller;

import com.pds.faultsreturnsservice.model.FaultReturn;
import com.pds.faultsreturnsservice.model.FaultReturnStatus;
import com.pds.faultsreturnsservice.service.FaultReturnService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faults-returns")
public class FaultReturnController {
    private final FaultReturnService service;

    public FaultReturnController(FaultReturnService service) {
        this.service = service;
    }

    @PostMapping
    public FaultReturn reportFault(@RequestBody FaultReturn faultReturn) {
        return service.reportFault(faultReturn);
    }

    @GetMapping
    public List<FaultReturn> getAll() {
        return service.getAll();
    }

    @PatchMapping("/{id}/status")
    public FaultReturn updateStatus(@PathVariable Long id, @RequestParam FaultReturnStatus status) {
        return service.updateStatus(id, status);
    }
}