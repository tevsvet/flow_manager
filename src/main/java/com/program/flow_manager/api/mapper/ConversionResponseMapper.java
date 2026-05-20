package com.program.flow_manager.api.mapper;

import com.program.flow_manager.api.dto.ConversionStatusResponse;
import com.program.flow_manager.api.dto.ConversionSubmitResponse;
import com.program.flow_manager.domain.model.ConversionTaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConversionResponseMapper {

    @Mapping(target = "taskId", source = "id")
    ConversionSubmitResponse toSubmitResponse(ConversionTaskEntity task);

    @Mapping(target = "taskId", source = "id")
    ConversionStatusResponse toStatusResponse(ConversionTaskEntity task);
}

