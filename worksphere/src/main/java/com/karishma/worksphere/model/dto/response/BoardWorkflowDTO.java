package com.karishma.worksphere.model.dto.response;

import com.karishma.worksphere.model.enums.IssueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardWorkflowDTO {
 private String workflow_name;
 private IssueType issue;
 private LocalDateTime createdAt;
}
