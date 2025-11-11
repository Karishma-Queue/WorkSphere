package com.karishma.worksphere.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class IssueResponse {

    private UUID issueId;
    private String summary;
    private String description;

    private String priority;
    private String issueType;
    private String status;

    private String boardName;
    private String workflowName;

    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    private UUID reporterId;
    private String reporterName;

    private UUID assigneeId;
    private String assigneeName;

    private UUID parentId;     // If this is subtask
    private UUID epicId;       // If linked to epic
}
