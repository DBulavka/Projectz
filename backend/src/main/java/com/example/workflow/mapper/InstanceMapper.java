package com.example.workflow.mapper;

import com.example.workflow.dto.instance.HistoricActivityDto;
import com.example.workflow.dto.instance.ProcessInstanceDto;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InstanceMapper {
    @Mapping(target = "processId", source = "processDefinitionId")
    ProcessInstanceDto toDto(ProcessInstance entity);

    List<ProcessInstanceDto> toDtoList(List<ProcessInstance> entities);

    HistoricActivityDto toDto(HistoricActivityInstance entity);

    List<HistoricActivityDto> toHistoryDtoList(List<HistoricActivityInstance> entities);
}
