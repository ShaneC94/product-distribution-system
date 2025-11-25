package com.pds.faultsreturnsservice.controller;

import com.pds.faultsreturnsservice.model.FaultReturn;
import com.pds.faultsreturnsservice.model.FaultReturnStatus;
import com.pds.faultsreturnsservice.service.FaultReturnService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faults")
public class FaultReturnController {

    private final FaultReturnService service;

    public FaultReturnController(FaultReturnService service) {
        this.service = service;
    }

    @PostMapping
    public FaultReturn create(@RequestBody FaultReturn req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    public FaultReturn get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/status/{status}")
    public List<FaultReturn> getByStatus(@PathVariable FaultReturnStatus status) {
        return service.listByStatus(status);
    }

    @GetMapping("/order/{orderId}")
    public List<FaultReturn> getByOrderId(@PathVariable Long orderId) {
        return service.listByOrderId(orderId);
    }

    @PostMapping("/{id}/inspect")
    public FaultReturn inspect(@PathVariable Long id) {
        return service.markInspected(id);
    }

    @PostMapping("/{id}/restock")
    public FaultReturn restock(@PathVariable Long id) {
        return service.markRestocked(id);
    }

    @PostMapping("/{id}/discard")
    public FaultReturn discard(@PathVariable Long id) {
        return service.markDiscarded(id);
    }
}
