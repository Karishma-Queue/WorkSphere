package com.workify.worksphere.model.dto.request;

import com.workify.worksphere.model.enums.IssueType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class WorkflowRequestDTO {
    private String workflow_name;
    private boolean isDefault;
    private IssueType issue;
}
