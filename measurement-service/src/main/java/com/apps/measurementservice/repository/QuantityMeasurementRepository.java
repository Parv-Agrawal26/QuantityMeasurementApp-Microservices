package com.apps.measurementservice.repository;

import com.apps.measurementservice.entity.QuantityMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuantityMeasurementRepository extends JpaRepository<QuantityMeasurementEntity, Long> {

    List<QuantityMeasurementEntity> findByUserEmail(String userEmail);

    List<QuantityMeasurementEntity> findByUserEmailAndOperation(String userEmail, String operation);

    long countByUserEmailAndOperationAndErrorFalse(String userEmail, String operation);
}
