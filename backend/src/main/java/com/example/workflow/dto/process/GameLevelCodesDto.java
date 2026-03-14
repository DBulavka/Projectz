package com.example.workflow.dto.process;

import java.util.List;

@lombok.Value
@lombok.Builder
public class GameLevelCodesDto {
    private String levelKey;
    private List<GameLevelCodeItemDto> codes;
}
