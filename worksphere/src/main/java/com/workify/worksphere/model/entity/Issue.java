package com.workify.worksphere.model.entity;

import com.workify.worksphere.model.enums.IssueType;
import com.workify.worksphere.model.enums.Priority;
import com.workify.worksphere.model.value.IssueId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Issue {

  @EmbeddedId
  private IssueId issueId;

  @PrePersist
  private void generateId() {
    if (this.issueId == null) {
      this.issueId = IssueId.generate();
    }
  }

  @Column(nullable=false)
  private String summary;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Priority priority;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IssueType issue;

  @CreationTimestamp
  @Column(nullable=false, updatable = false)
  private LocalDateTime createdAt;

  private LocalDate dueDate;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  private LocalDateTime resolvedAt;

  @ManyToOne
  @JoinColumn(name="reporter_id", nullable=false)
  private User reporter;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="assignee_id")
  private User assignee;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id", nullable = false)
  private Board board;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "workflow_id", nullable = false)
  private Workflow workflow;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "status_id", nullable = false)
  private WorkflowStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Issue parent;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sprint_id")
  private Sprint sprint;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Issue> subtasks;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "epic_id")
  private Issue epic;

  @OneToMany(mappedBy = "epic", cascade = CascadeType.ALL)
  private List<Issue> epicChildren;
}
