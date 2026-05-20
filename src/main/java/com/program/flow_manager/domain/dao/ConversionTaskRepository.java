package com.program.flow_manager.domain.dao;

import com.program.flow_manager.domain.model.ConversionTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConversionTaskRepository extends JpaRepository<ConversionTaskEntity, UUID> {
}
