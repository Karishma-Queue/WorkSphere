package com.workify.worksphere.model.dto.response;

import com.workify.worksphere.model.enums.IssueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WorkflowResponse {
   private String id;
   private String workflow_name;
   private IssueType issue;
   private LocalDateTime createdAt;
}
