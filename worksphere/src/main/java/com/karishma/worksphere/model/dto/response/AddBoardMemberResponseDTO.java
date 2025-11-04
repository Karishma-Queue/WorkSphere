package com.karishma.worksphere.model.dto.response;

import com.karishma.worksphere.model.entity.Board;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.model.enums.BoardRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddBoardMemberResponseDTO {
    private UUID board_member_id;
    private Board board;
    private User user;
    private BoardRole boardRole;
    private LocalDateTime joinedAt;

}
