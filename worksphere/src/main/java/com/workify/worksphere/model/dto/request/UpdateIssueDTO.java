package com.workify.worksphere.model.dto.request;

import com.workify.worksphere.model.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateIssueDTO {
    private String summary;
    private String description;
    private Priority priority;
    private String assignee_id;
    private LocalDate due_date;
}
