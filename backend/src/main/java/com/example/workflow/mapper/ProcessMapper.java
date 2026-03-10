package com.example.workflow.mapper;

import com.example.workflow.dto.audit.AuditLogDto;
import com.example.workflow.dto.process.ProcessMetaDto;
import com.example.workflow.dto.process.ProcessVersionDto;
import com.example.workflow.entity.AuditLog;
import com.example.workflow.entity.ProcessDefinitionMeta;
import com.example.workflow.entity.ProcessDefinitionVersion;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProcessMapper {
    ProcessMetaDto toDto(ProcessDefinitionMeta entity);

    List<ProcessMetaDto> toMetaDtoList(List<ProcessDefinitionMeta> entities);

    ProcessVersionDto toDto(ProcessDefinitionVersion entity);

    List<ProcessVersionDto> toVersionDtoList(List<ProcessDefinitionVersion> entities);

    AuditLogDto toDto(AuditLog entity);

    List<AuditLogDto> toAuditDtoList(List<AuditLog> entities);
}
