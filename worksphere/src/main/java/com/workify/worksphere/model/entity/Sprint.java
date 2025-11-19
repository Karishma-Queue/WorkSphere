package com.workify.worksphere.model.entity;

import com.workify.worksphere.model.enums.SprintStatus;
import com.workify.worksphere.model.value.SprintId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sprint {
  @EmbeddedId
  private SprintId sprintId;
  @Column(nullable = false)
  private String sprintName;
  @Column(nullable = false)
  private LocalDate startDate;
  @Column(nullable=false)
  private LocalDate endDate;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SprintStatus status;
  @ManyToOne
  @JoinColumn(name = "board_id", nullable = false)
  private Board board;
  @ManyToOne
  @JoinColumn(name="created_by",nullable=false)
  private User createdBy;
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  private LocalDateTime startedAt;

  private LocalDateTime completedAt;


}
