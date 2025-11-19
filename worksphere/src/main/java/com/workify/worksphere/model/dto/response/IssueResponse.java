package com.workify.worksphere.model.dto.response;

import com.workify.worksphere.model.entity.User;
import com.workify.worksphere.model.value.IssueId;
import com.workify.worksphere.model.value.UserId;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class IssueResponse {

    private IssueId issueId;
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

    private UserId reporterId;
    private String reporterName;

    private UserId assigneeId;
    private String assigneeName;

    private IssueId parentId;     // If this is subtask
    private IssueId epicId;       // If linked to epic
}
