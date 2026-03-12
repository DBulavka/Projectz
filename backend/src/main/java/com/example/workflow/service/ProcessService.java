package com.example.workflow.service;

import com.example.workflow.dto.process.*;
import com.example.workflow.entity.AuditLog;
import com.example.workflow.entity.GameCodeDifficulty;
import com.example.workflow.entity.GameLevelCode;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.AuditLogRepository;
import com.example.workflow.repository.GameCodeDifficultyRepository;
import com.example.workflow.repository.GameLevelCodeRepository;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessService {
    private final AuditLogRepository auditLogRepository;
    private final RepositoryService repositoryService;
    private final AuditService auditService;
    private final GameLevelCodeRepository gameLevelCodeRepository;
    private final GameCodeDifficultyRepository gameCodeDifficultyRepository;

    public List<ProcessDefinition> list() {
        return repositoryService.createProcessDefinitionQuery().latestVersion().list();
    }

    public ProcessDefinition create(ProcessMetaRequest req) {
        String key = req.key();
        String name = req.name();
        String bpmn = """
                <?xml version=\"1.0\" encoding=\"UTF-8\"?>
                <definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" targetNamespace=\"Examples\">
                  <process id=\"%s\" name=\"%s\" isExecutable=\"true\">
                    <startEvent id=\"start\"/>
                    <endEvent id=\"end\"/>
                    <sequenceFlow id=\"flow1\" sourceRef=\"start\" targetRef=\"end\"/>
                  </process>
                </definitions>
                """.formatted(key, name);
        Deployment deployment = repositoryService.createDeployment()
                .name("process-" + key)
                .category(req.category())
                .addString(key + ".bpmn20.xml", bpmn)
                .deploy();
        ProcessDefinition created = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        auditService.log(null, "PROCESS", created.getId(), "CREATE", req);
        return created;
    }

    public ProcessDefinition get(String id) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionId(id).singleResult();
        if (definition == null) {
            throw new ApiException("Process not found");
        }
        return definition;
    }

    public ProcessDefinition update(String id, ProcessMetaRequest req) {
        throw new ApiException("Update is not supported for Flowable process definitions");
    }

    public void delete(String id) {
        ProcessDefinition definition = get(id);
        repositoryService.deleteDeployment(definition.getDeploymentId(), true);
    }

    public List<ProcessDefinition> listVersions(String processId) {
        ProcessDefinition process = get(processId);
        return repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(process.getKey())
                .orderByProcessDefinitionVersion().desc()
                .list();
    }

    public ProcessDefinition createVersion(String processId, ProcessVersionRequest req) {
        ProcessDefinition process = get(processId);
        Deployment deployment = repositoryService.createDeployment()
                .name("process-" + process.getKey() + "-v-next")
                .addString(process.getKey() + ".bpmn20.xml", req.bpmnXml())
                .deploy();
        return repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
    }

    public ProcessDefinition getVersion(String processId, String versionId) {
        ProcessDefinition process = get(processId);
        ProcessDefinition version = repositoryService.createProcessDefinitionQuery().processDefinitionId(versionId).singleResult();
        if (version == null || !process.getKey().equals(version.getKey())) {
            throw new ApiException("Version not found");
        }
        return version;
    }

    public ProcessDefinition updateVersionBpmn(String processId, String versionId, ProcessVersionRequest req) {
        ProcessDefinition version = getVersion(processId, versionId);
        Deployment deployment = repositoryService.createDeployment()
                .name("process-" + version.getKey() + "-v" + version.getVersion())
                .addString(version.getKey() + ".bpmn20.xml", req.bpmnXml())
                .deploy();
        return repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
    }

    public ProcessDefinition publish(String processId, String versionId) {
        return getVersion(processId, versionId);
    }

    public List<AuditLog> processAudit(String processId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc("PROCESS", processId);
    }

    public List<GameLevelCodeItemDto> getLevelCodes(String processId, String levelKey) {
        return gameLevelCodeRepository.findByProcessIdAndLevelKeyOrderByCreatedAtAsc(processId, levelKey).stream()
                .map(this::toGameLevelCodeItemDto)
                .toList();
    }

    public List<GameLevelCodeItemDto> replaceLevelCodes(String processId, String levelKey, GameLevelCodesRequest req) {
        List<GameLevelCodeItemRequest> normalizedCodes = req.codes().stream()
                .map(code -> new GameLevelCodeItemRequest(
                        code.value().trim(),
                        normalizeNullable(code.description()),
                        code.difficultyValue().trim(),
                        normalizeNullable(code.difficultyDescription())
                ))
                .filter(code -> !code.value().isEmpty() && !code.difficultyValue().isEmpty())
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toMap(
                                code -> code.value() + "::" + code.difficultyValue(),
                                code -> code,
                                (left, right) -> left,
                                java.util.LinkedHashMap::new
                        ),
                        map -> new ArrayList<>(map.values())
                ));

        if (normalizedCodes.isEmpty()) {
            throw new ApiException("At least one non-empty code with difficulty is required");
        }

        List<GameLevelCode> existing = gameLevelCodeRepository.findByProcessIdAndLevelKeyOrderByCreatedAtAsc(processId, levelKey);
        gameLevelCodeRepository.deleteAll(existing);

        Instant now = Instant.now();
        List<GameLevelCode> toSave = normalizedCodes.stream()
                .map(code -> {
                    GameCodeDifficulty difficulty = findOrCreateDifficulty(code.difficultyValue(), code.difficultyDescription(), now);
                    return GameLevelCode.builder()
                            .processId(processId)
                            .levelKey(levelKey)
                            .value(code.value())
                            .description(code.description())
                            .difficultyId(difficulty.getId())
                            .createdAt(now)
                            .build();
                })
                .toList();

        return gameLevelCodeRepository.saveAll(toSave).stream()
                .map(this::toGameLevelCodeItemDto)
                .toList();
    }

    private GameCodeDifficulty findOrCreateDifficulty(String value, String description, Instant now) {
        return gameCodeDifficultyRepository.findByValue(value)
                .map(existing -> {
                    String normalizedDescription = normalizeNullable(description);
                    if (normalizedDescription != null && !normalizedDescription.equals(existing.getDescription())) {
                        existing.setDescription(normalizedDescription);
                        existing.setUpdatedAt(now);
                        return gameCodeDifficultyRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> gameCodeDifficultyRepository.save(GameCodeDifficulty.builder()
                        .value(value)
                        .description(normalizeNullable(description))
                        .createdAt(now)
                        .updatedAt(now)
                        .build()));
    }

    private GameLevelCodeItemDto toGameLevelCodeItemDto(GameLevelCode code) {
        GameCodeDifficulty difficulty = gameCodeDifficultyRepository.findById(code.getDifficultyId())
                .orElseThrow(() -> new ApiException("Code difficulty not found"));
        return new GameLevelCodeItemDto(
                code.getValue(),
                code.getDescription(),
                new GameCodeDifficultyDto(difficulty.getValue(), difficulty.getDescription())
        );
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
