package com.workify.worksphere.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class IssueResponse {

    private String issueId;
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

    private String reporterId;
    private String reporterName;

    private String assigneeId;
    private String assigneeName;

    private String parentId;     // If this is subtask
    private String epicId;       // If linked to epic
}
