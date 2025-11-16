package com.workify.worksphere.model.dto.response;

import com.workify.worksphere.model.enums.BoardRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllBoardMemberDTO {
    private String board_member_id;
    private String user_name;
    private String user_id;
    private BoardRole boardRole;
    private LocalDateTime joinedAt;
}
