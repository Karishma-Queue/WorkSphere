package com.workify.worksphere.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransitionRequest {
    @NotBlank(message = "FROM Status_id cannot be left blank")
   private String from_status_id;
    @NotBlank(message="TO Status_id cannot be left blank")
   private String to_status_id;
    @NotEmpty(message = "At least one role must be specified")
    private Set<String> allowedRoles;

}
