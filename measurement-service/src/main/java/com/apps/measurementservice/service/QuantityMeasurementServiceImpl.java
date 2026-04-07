package com.apps.measurementservice.service;

import com.apps.measurementservice.dto.QuantityDTO;
import com.apps.measurementservice.dto.QuantityInputDTO;
import com.apps.measurementservice.entity.QuantityMeasurementEntity;
import com.apps.measurementservice.exception.QuantityMeasurementException;
import com.apps.measurementservice.repository.QuantityMeasurementRepository;
import com.apps.measurementservice.unit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    @Autowired
    private QuantityMeasurementRepository repository;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private IMeasurable getUnit(String measurementType, String unit) {
        return switch (measurementType.toUpperCase()) {
            case "LENGTH" -> LengthUnit.valueOf(unit.toUpperCase());
            case "WEIGHT" -> WeightUnit.valueOf(unit.toUpperCase());
            case "VOLUME" -> VolumeUnit.valueOf(unit.toUpperCase());
            case "TEMPERATURE" -> TemperatureUnit.valueOf(unit.toUpperCase());
            default -> throw new QuantityMeasurementException("Invalid measurement type: " + measurementType);
        };
    }

    private void setCommonFields(QuantityMeasurementEntity entity, QuantityInputDTO input) {
        QuantityDTO q1 = input.getThisQuantityDTO();
        QuantityDTO q2 = input.getThatQuantityDTO();

        entity.setThisValue(q1.getValue());
        entity.setThisUnit(q1.getUnit());
        entity.setThisMeasurementType(q1.getMeasurementType());

        if (q2 != null) {
            entity.setThatValue(q2.getValue());
            entity.setThatUnit(q2.getUnit());
            entity.setThatMeasurementType(q2.getMeasurementType());
        }
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUserEmail(getCurrentUserEmail());
    }

    @Override
    public QuantityMeasurementEntity compare(QuantityInputDTO input) {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        try {
            QuantityDTO q1 = input.getThisQuantityDTO();
            QuantityDTO q2 = input.getThatQuantityDTO();
            double base1 = getUnit(q1.getMeasurementType(), q1.getUnit()).convertToBaseUnit(q1.getValue());
            double base2 = getUnit(q2.getMeasurementType(), q2.getUnit()).convertToBaseUnit(q2.getValue());
            setCommonFields(entity, input);
            entity.setOperation("COMPARE");
            entity.setResultString(String.valueOf(base1 == base2));
        } catch (Exception e) {
            entity.setError(true);
            entity.setErrorMessage(e.getMessage());
            entity.setOperation("COMPARE");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUserEmail(getCurrentUserEmail());
        }
        return repository.save(entity);
    }

    @Override
    public QuantityMeasurementEntity convert(QuantityInputDTO input) {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        try {
            QuantityDTO q = input.getThisQuantityDTO();
            QuantityDTO target = input.getThatQuantityDTO();
            IMeasurable fromUnit = getUnit(q.getMeasurementType(), q.getUnit());
            IMeasurable toUnit = getUnit(q.getMeasurementType(), target.getUnit());
            double result = toUnit.convertFromBaseUnit(fromUnit.convertToBaseUnit(q.getValue()));
            entity.setThisValue(q.getValue());
            entity.setThisUnit(q.getUnit());
            entity.setThisMeasurementType(q.getMeasurementType());
            entity.setOperation("CONVERT");
            entity.setResultValue(result);
            entity.setResultUnit(target.getUnit());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUserEmail(getCurrentUserEmail());
        } catch (Exception e) {
            entity.setError(true);
            entity.setErrorMessage(e.getMessage());
            entity.setOperation("CONVERT");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUserEmail(getCurrentUserEmail());
        }
        return repository.save(entity);
    }

    @Override
    public QuantityMeasurementEntity add(QuantityInputDTO input) {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        try {
            QuantityDTO q1 = input.getThisQuantityDTO();
            QuantityDTO q2 = input.getThatQuantityDTO();
            IMeasurable unit1 = getUnit(q1.getMeasurementType(), q1.getUnit());
            IMeasurable unit2 = getUnit(q2.getMeasurementType(), q2.getUnit());
            if (!unit1.supportsArithmetic() || !unit2.supportsArithmetic())
                throw new QuantityMeasurementException("Arithmetic not supported for this unit");
            double result = unit1.convertFromBaseUnit(unit1.convertToBaseUnit(q1.getValue()) + unit2.convertToBaseUnit(q2.getValue()));
            setCommonFields(entity, input);
            entity.setOperation("ADD");
            entity.setResultValue(result);
            entity.setResultUnit(q1.getUnit());
        } catch (Exception e) {
            entity.setError(true);
            entity.setErrorMessage(e.getMessage());
            entity.setOperation("ADD");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUserEmail(getCurrentUserEmail());
        }
        return repository.save(entity);
    }

    @Override
    public QuantityMeasurementEntity subtract(QuantityInputDTO input) {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        try {
            QuantityDTO q1 = input.getThisQuantityDTO();
            QuantityDTO q2 = input.getThatQuantityDTO();
            IMeasurable unit1 = getUnit(q1.getMeasurementType(), q1.getUnit());
            IMeasurable unit2 = getUnit(q2.getMeasurementType(), q2.getUnit());
            if (!unit1.supportsArithmetic() || !unit2.supportsArithmetic())
                throw new QuantityMeasurementException("Arithmetic not supported for this unit");
            double result = unit1.convertFromBaseUnit(unit1.convertToBaseUnit(q1.getValue()) - unit2.convertToBaseUnit(q2.getValue()));
            setCommonFields(entity, input);
            entity.setOperation("SUBTRACT");
            entity.setResultValue(result);
            entity.setResultUnit(q1.getUnit());
        } catch (Exception e) {
            entity.setError(true);
            entity.setErrorMessage(e.getMessage());
            entity.setOperation("SUBTRACT");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUserEmail(getCurrentUserEmail());
        }
        return repository.save(entity);
    }

    @Override
    public QuantityMeasurementEntity divide(QuantityInputDTO input) {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        try {
            QuantityDTO q1 = input.getThisQuantityDTO();
            QuantityDTO q2 = input.getThatQuantityDTO();
            IMeasurable unit1 = getUnit(q1.getMeasurementType(), q1.getUnit());
            IMeasurable unit2 = getUnit(q2.getMeasurementType(), q2.getUnit());
            if (!unit1.supportsArithmetic() || !unit2.supportsArithmetic())
                throw new QuantityMeasurementException("Arithmetic not supported for this unit");
            double base1 = unit1.convertToBaseUnit(q1.getValue());
            double base2 = unit2.convertToBaseUnit(q2.getValue());
            if (base2 == 0) throw new QuantityMeasurementException("Cannot divide by zero");
            setCommonFields(entity, input);
            entity.setOperation("DIVIDE");
            entity.setResultValue(base1 / base2);
            entity.setResultUnit("RATIO");
        } catch (Exception e) {
            entity.setError(true);
            entity.setErrorMessage(e.getMessage());
            entity.setOperation("DIVIDE");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUserEmail(getCurrentUserEmail());
        }
        return repository.save(entity);
    }

    @Override
    public List<QuantityMeasurementEntity> getHistory() {
        return repository.findByUserEmail(getCurrentUserEmail());
    }

    @Override
    public List<QuantityMeasurementEntity> getHistoryByOperation(String operation) {
        return repository.findByUserEmailAndOperation(getCurrentUserEmail(), operation.toUpperCase());
    }

    @Override
    public long getOperationCount(String operation) {
        return repository.countByUserEmailAndOperationAndErrorFalse(getCurrentUserEmail(), operation.toUpperCase());
    }
}
