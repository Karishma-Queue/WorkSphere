package com.workify.worksphere.model.dto.response;

import com.workify.worksphere.model.enums.SprintStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SprintResponse {
  private String sprintId;
  private String springName;
  private LocalDate startDate;
  private LocalDate endDate;
  private String boardId;
  private SprintStatus sprintStatus;
}
