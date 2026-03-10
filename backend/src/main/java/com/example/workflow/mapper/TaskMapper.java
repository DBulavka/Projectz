package com.example.workflow.mapper;

import com.example.workflow.dto.task.HistoricTaskDto;
import com.example.workflow.dto.task.TaskDto;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDto toDto(Task task);

    List<TaskDto> toTaskDtoList(List<Task> tasks);

    HistoricTaskDto toDto(HistoricTaskInstance task);

    List<HistoricTaskDto> toHistoricTaskDtoList(List<HistoricTaskInstance> tasks);
}
