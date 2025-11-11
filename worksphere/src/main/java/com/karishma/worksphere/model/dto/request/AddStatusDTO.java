package com.karishma.worksphere.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddStatusDTO {
    private Boolean started;
    private Boolean ended;
    private Boolean is_initial;
    private String status_name;
}
