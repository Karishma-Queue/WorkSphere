package com.karishma.worksphere.model.dto.request;

import com.karishma.worksphere.model.enums.IssueType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class WorkflowRequestDTO {
    private String workflow_name;
    private IssueType issue;
}
