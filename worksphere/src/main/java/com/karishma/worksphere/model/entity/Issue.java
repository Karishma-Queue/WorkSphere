package com.karishma.worksphere.model.entity;

import com.karishma.worksphere.model.enums.IssueType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String summary;
    private String description;
    private Priority priority;
    private IssueType issue;
    @ManyToOne
    @JoinColumn()
}
