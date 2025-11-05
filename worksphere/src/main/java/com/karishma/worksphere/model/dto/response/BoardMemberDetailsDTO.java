package com.karishma.worksphere.model.dto.response;

import com.karishma.worksphere.model.enums.BoardRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardMemberDetailsDTO
{
  private UUID user_id;
  private String user_name;
  private String email;
  private String job_title;
  private String department;
  private String profile_picture_url;
  private UUID board_id;
  private String board_name;
  private String board_key;
  private BoardRole boardRole;
  private LocalDateTime joinedAt;
}
