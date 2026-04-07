package com.apps.measurementservice.controller;

import com.apps.measurementservice.dto.QuantityInputDTO;
import com.apps.measurementservice.entity.QuantityMeasurementEntity;
import com.apps.measurementservice.service.IQuantityMeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurements", description = "REST API for quantity measurement operations")
public class QuantityMeasurementController {

    @Autowired
    private IQuantityMeasurementService service;

    @PostMapping("/compare")
    @Operation(summary = "Compare two quantities")
    public ResponseEntity<QuantityMeasurementEntity> compare(@RequestBody QuantityInputDTO input) {
        return ResponseEntity.ok(service.compare(input));
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert a quantity to another unit")
    public ResponseEntity<QuantityMeasurementEntity> convert(@RequestBody QuantityInputDTO input) {
        return ResponseEntity.ok(service.convert(input));
    }

    @PostMapping("/add")
    @Operation(summary = "Add two quantities")
    public ResponseEntity<QuantityMeasurementEntity> add(@RequestBody QuantityInputDTO input) {
        return ResponseEntity.ok(service.add(input));
    }

    @PostMapping("/subtract")
    @Operation(summary = "Subtract two quantities")
    public ResponseEntity<QuantityMeasurementEntity> subtract(@RequestBody QuantityInputDTO input) {
        return ResponseEntity.ok(service.subtract(input));
    }

    @PostMapping("/divide")
    @Operation(summary = "Divide two quantities")
    public ResponseEntity<QuantityMeasurementEntity> divide(@RequestBody QuantityInputDTO input) {
        return ResponseEntity.ok(service.divide(input));
    }

    @GetMapping("/history")
    @Operation(summary = "Get all operation history")
    public ResponseEntity<List<QuantityMeasurementEntity>> getHistory() {
        return ResponseEntity.ok(service.getHistory());
    }

    @GetMapping("/history/{operation}")
    @Operation(summary = "Get history by operation type")
    public ResponseEntity<List<QuantityMeasurementEntity>> getHistoryByOperation(@PathVariable String operation) {
        return ResponseEntity.ok(service.getHistoryByOperation(operation));
    }

    @GetMapping("/count/{operation}")
    @Operation(summary = "Get count of successful operations by type")
    public ResponseEntity<Long> getOperationCount(@PathVariable String operation) {
        return ResponseEntity.ok(service.getOperationCount(operation));
    }
}
