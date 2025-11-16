package com.workify.worksphere.model.dto.request;

import com.workify.worksphere.model.enums.BoardRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddBoardMemberDTO {
    @NotNull
    @NotBlank
    private String email;
    @Builder.Default
    private BoardRole boardRole=BoardRole.PROJECT_MEMBER;

}
