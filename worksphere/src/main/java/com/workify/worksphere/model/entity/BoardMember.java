package com.workify.worksphere.model.entity;

import com.workify.worksphere.model.enums.BoardRole;
import com.workify.worksphere.model.value.BoardMemberId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardMember {

  @EmbeddedId
  private BoardMemberId boardMemberId;

  @PrePersist
  private void generateId() {
    if (this.boardMemberId == null) {
      this.boardMemberId = BoardMemberId.generate();
    }
  }

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "board_id", nullable = false)
  private Board board;

  @Enumerated(EnumType.STRING)
  @Column(name = "board_role", nullable = false)
  @Builder.Default
  private BoardRole boardRole = BoardRole.PROJECT_MEMBER;

  @CreationTimestamp
  @Column(name = "joined_at", nullable = false, updatable = false)
  private LocalDateTime joinedAt;
}
