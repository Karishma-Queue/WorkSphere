package com.karishma.worksphere.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransitionRequest {
    @NotBlank(message = "FROM Status_id cannot be left blank")
   private UUID from_status_id;
    @NotBlank(message="TO Status_id cannot be left blank")
   private UUID to_status_id;
    @NotEmpty(message = "At least one role must be specified")
    private Set<String> allowedRoles;

}
