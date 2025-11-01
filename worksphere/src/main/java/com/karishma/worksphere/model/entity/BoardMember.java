package com.karishma.worksphere.model.entity;

import com.karishma.worksphere.model.enums.BoardRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID board_member_id;
    @ManyToOne
    @JoinColumn(name="user_id",nullable=false)
    private User user;
    @ManyToOne
    @JoinColumn(name="board_id",nullable=false)
    private Board board;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BoardRole boardRole= BoardRole.PROJECT_MEMBER;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;
}
