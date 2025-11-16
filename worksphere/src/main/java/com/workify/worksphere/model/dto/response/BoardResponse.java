package com.workify.worksphere.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponse {
    private String boardId;
    private String boardName;
    private String boardKey;
    private String description;
    private LocalDateTime createdAt;

    private String createdByUserId;
    private String createdByUserName;

}
