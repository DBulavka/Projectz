package com.example.workflow.mapper;

import com.example.workflow.dto.task.HistoricTaskDto;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    HistoricTaskDto toDto(HistoricTaskInstance task);

    List<HistoricTaskDto> toHistoricTaskDtoList(List<HistoricTaskInstance> tasks);
}
