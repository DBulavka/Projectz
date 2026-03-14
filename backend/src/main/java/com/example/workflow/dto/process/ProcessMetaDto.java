package com.example.workflow.dto.process;

@lombok.Value
@lombok.Builder
public class ProcessMetaDto {
    private String id;
    private String name;
    private String description;
    private int version;
    private String deploymentId;
}
