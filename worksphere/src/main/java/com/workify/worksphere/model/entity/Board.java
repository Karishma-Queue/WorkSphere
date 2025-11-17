package com.workify.worksphere.model.entity;

import com.workify.worksphere.model.enums.BoardStatus;
import com.workify.worksphere.model.value.BoardId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board {

  @EmbeddedId
  private BoardId boardId;

  @Column(name = "board_name", nullable = false)
  private String boardName;

  @Column(name = "board_key", nullable = false)
  private String boardKey;

  @Column(nullable = false)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "board_status")
  @Builder.Default
  private BoardStatus boardStatus = BoardStatus.ACTIVE;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "created_by", nullable = false)
  private User createdBy;

  @PrePersist
  public void generateId() {
    if (this.boardId == null) {
      this.boardId = BoardId.generate();
    }
  }
}
