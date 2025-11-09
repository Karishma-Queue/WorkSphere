package com.karishma.worksphere.model.dto.response;

import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.model.enums.IssueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WorkflowResponse {
   private UUID id;
   private String workflow_name;
   private IssueType issue;
   private LocalDateTime createdAt;
}
