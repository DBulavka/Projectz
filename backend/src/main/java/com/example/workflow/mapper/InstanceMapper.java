package com.example.workflow.mapper;

import com.example.workflow.dto.instance.HistoricActivityDto;
import com.example.workflow.dto.instance.ProcessInstanceDto;
import com.example.workflow.entity.ProcessInstanceMeta;
import org.flowable.engine.history.HistoricActivityInstance;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InstanceMapper {
    ProcessInstanceDto toDto(ProcessInstanceMeta entity);

    List<ProcessInstanceDto> toDtoList(List<ProcessInstanceMeta> entities);

    HistoricActivityDto toDto(HistoricActivityInstance entity);

    List<HistoricActivityDto> toHistoryDtoList(List<HistoricActivityInstance> entities);
}
