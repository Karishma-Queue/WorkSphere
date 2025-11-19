package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.Sprint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SprintRepository extends JpaRepository<Sprint,String> {
  Optional<Sprint> findBySprintId(String sprintId);
  Optional<Sprint> findBySprintName(String name);
  List<Sprint> findByBoard_BoardId(String boardId);
}
