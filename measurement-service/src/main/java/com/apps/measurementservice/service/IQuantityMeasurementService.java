package com.apps.measurementservice.service;

import com.apps.measurementservice.dto.QuantityInputDTO;
import com.apps.measurementservice.entity.QuantityMeasurementEntity;

import java.util.List;

public interface IQuantityMeasurementService {
    QuantityMeasurementEntity compare(QuantityInputDTO input);
    QuantityMeasurementEntity convert(QuantityInputDTO input);
    QuantityMeasurementEntity add(QuantityInputDTO input);
    QuantityMeasurementEntity subtract(QuantityInputDTO input);
    QuantityMeasurementEntity divide(QuantityInputDTO input);
    List<QuantityMeasurementEntity> getHistory();
    List<QuantityMeasurementEntity> getHistoryByOperation(String operation);
    long getOperationCount(String operation);
}
