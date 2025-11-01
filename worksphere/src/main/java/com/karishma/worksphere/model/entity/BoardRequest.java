package com.karishma.worksphere.model.entity;

import com.karishma.worksphere.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class BoardRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID board_request_id;
    @Column(nullable = false,unique = true)
    private String board_name;
    @Column(nullable = false,unique=true)
    private String board_key;
    @Column(nullable = false)
    private String description;

    private String justification;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private Status status=Status.PENDING;
    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime requestedAt;
    @OneToOne
    @JoinColumn(name="requester_id",nullable = false)
    private User requester;
    @OneToOne
    @JoinColumn(name="reviewer_id")
    private User reviewedBy;
    private LocalDateTime rejectedAt;
    private LocalDateTime approvedAt;
    private String rejection_reason;


}
