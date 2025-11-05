package com.karishma.worksphere.model.dto.response;

import com.karishma.worksphere.model.enums.BoardRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllBoardMemberDTO {
    private UUID board_member_id;
    private String user_name;
    private UUID user_id;
    private BoardRole boardRole;
    private LocalDateTime joinedAt;
}
