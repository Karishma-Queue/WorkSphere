package com.karishma.worksphere.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkflowUpdateDTO {
    private String workflow_name;
    private Boolean isDefault;


}
