package com.example.workflow.mapper;

import com.example.workflow.dto.audit.AuditLogDto;
import com.example.workflow.dto.process.ProcessMetaDto;
import com.example.workflow.dto.process.ProcessVersionDto;
import com.example.workflow.entity.AuditLog;
import org.flowable.engine.repository.ProcessDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProcessMapper {

    ProcessMetaDto toDto(ProcessDefinition entity);

    List<ProcessMetaDto> toMetaDtoList(List<ProcessDefinition> entities);

    @Mapping(target = "processDefinitionMetaId", source = "key")
    @Mapping(target = "status", constant = "PUBLISHED")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    ProcessVersionDto toVersionDto(ProcessDefinition entity);

    List<ProcessVersionDto> toVersionDtoList(List<ProcessDefinition> entities);

    AuditLogDto toDto(AuditLog entity);

    List<AuditLogDto> toAuditDtoList(List<AuditLog> entities);
}
