package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.Sprint;
import com.workify.worksphere.model.enums.SprintStatus;
import com.workify.worksphere.model.value.BoardId;
import com.workify.worksphere.model.value.SprintId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SprintRepository extends JpaRepository<Sprint, SprintId> {
  Optional<Sprint> findBySprintId(SprintId sprintId);
  List<Sprint> findByBoard_BoardId(BoardId boardId);
  boolean existsByBoard_BoardIdAndSprintName(BoardId boardId, String sprintName);
  boolean existsByBoard_BoardIdAndStatus(BoardId boardId, SprintStatus status);
}
