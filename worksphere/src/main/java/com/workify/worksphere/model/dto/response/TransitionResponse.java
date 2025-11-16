package com.workify.worksphere.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransitionResponse {
    private String from_status;
    private String to_status;
    private String workflow_id;
}
