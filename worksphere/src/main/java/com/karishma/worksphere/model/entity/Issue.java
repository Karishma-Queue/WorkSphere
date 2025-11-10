package com.karishma.worksphere.model.entity;

import com.karishma.worksphere.model.enums.IssueType;
import com.karishma.worksphere.model.enums.Priority;
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
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Issue {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID issue_id;
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
    @Column(nullable=false,updatable = false)
    private LocalDateTime createdAt;
    private LocalDate due_date;
    @UpdateTimestamp
    private LocalDateTime updated_at;
    private LocalDateTime resolvedAt;
    @ManyToOne
    @JoinColumn(name="reporter_id",nullable=false)
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
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Issue> subtasks;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epic_id")
    private Issue epic;
    @OneToMany(mappedBy = "epic", cascade = CascadeType.ALL)
    private List<Issue> epic_children;

}
