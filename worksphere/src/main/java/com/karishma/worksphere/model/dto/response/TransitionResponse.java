package com.karishma.worksphere.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransitionResponse {
    private String from_status;
    private String to_status;
    private UUID workflow_id;
}
