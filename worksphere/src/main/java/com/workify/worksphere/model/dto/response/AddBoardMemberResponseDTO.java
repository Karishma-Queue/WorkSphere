package com.workify.worksphere.model.dto.response;

import com.workify.worksphere.model.entity.Board;
import com.workify.worksphere.model.entity.User;
import com.workify.worksphere.model.enums.BoardRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddBoardMemberResponseDTO {
    private String board_member_id;
    private Board board;
    private User user;
    private BoardRole boardRole;
    private LocalDateTime joinedAt;

}
