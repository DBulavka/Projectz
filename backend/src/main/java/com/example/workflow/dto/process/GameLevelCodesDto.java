package com.example.workflow.dto.process;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GameLevelCodesDto {
    String levelKey;
    List<GameLevelCodeItemDto> codes;
}
