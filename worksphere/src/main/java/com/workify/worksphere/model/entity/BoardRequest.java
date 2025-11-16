package com.workify.worksphere.model.entity;

import com.workify.worksphere.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class BoardRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String boardRequestId;

    @Column(name="board_request_name", nullable = false, unique = true)
    private String boardRequestName;

    @Column(name="board_request_key", nullable = false, unique = true)
    private String boardRequestKey;

    @Column(nullable = false)
    private String description;

    private String justification;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @ManyToOne
    @JoinColumn(name="requester_id", nullable = false)
    private User requester;

    @ManyToOne
    @JoinColumn(name="reviewer_id")
    private User reviewedBy;

    private LocalDateTime rejectedAt;
    private LocalDateTime approvedAt;
    private String rejectionReason;
}
