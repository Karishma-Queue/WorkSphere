package com.karishma.worksphere.model.dto.request;

import com.karishma.worksphere.model.enums.IssueType;
import com.karishma.worksphere.model.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateIssueDTO
{

        @NotNull(message="Issue type is required")
        private IssueType issue_type;
        @NotBlank(message="Summary is required")
        private String summary;
        @NotNull(message="Priority is required")
        private Priority priority;
        private String assignee_id;
        private String description;
        private String parent_id;
        private String epic_id;
        private LocalDate due_Date;


}



