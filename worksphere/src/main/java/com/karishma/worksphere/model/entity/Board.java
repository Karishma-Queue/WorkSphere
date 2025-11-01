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
    private String board_name;
    private String description;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BoardStatus boardStatus= BoardStatus.TO_DO;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @OneToOne
    @JoinColumn(name="created_by")
    private User createdBy;

}
