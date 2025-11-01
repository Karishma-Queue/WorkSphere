package com.karishma.worksphere.model.entity;

import com.karishma.worksphere.model.enums.BoardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID board_id;
    @Column(nullable = false)
    private String board_name;
    @Column(nullable=false)
    private String board_key;
    @Column(nullable = false)
    private String description;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BoardStatus boardStatus= BoardStatus.ACTIVE;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name="created_by",nullable = false)
    private User createdBy;

}
