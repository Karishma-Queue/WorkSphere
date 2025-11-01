package com.karishma.worksphere.model.entity;

import com.karishma.worksphere.model.enums.BoardRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @OneToOne
    @JoinColumn(name="user_id")
    private User user;
    @OneToOne
    @JoinColumn(name="board_id")
    private Board board;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BoardRole boardRole= BoardRole.PROJECT_MEMBER;
    private LocalDateTime joinedAt;
}
