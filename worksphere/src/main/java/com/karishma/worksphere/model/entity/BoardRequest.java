package com.karishma.worksphere.model.entity;

import com.karishma.worksphere.model.enums.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
public class BoardRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID board_request_id;
    @Column(nullable = false,unique = true)
    private String project_name;
    @Column(nullable = false)
    private String project_key;
    @Column(nullable = false)
    private String description;

    private String justification;
    @Enumerated(EnumType.STRING)
    private Status status=Status.PENDING
            ;
    @CreationTimestamp
    private LocalDateTime requestedAt;
    @OneToOne
    @JoinColumn(name="requester_id")
    private User requester;
    @OneToOne
    @JoinColumn(name="reviewer_id")
    private User reviewedBy;
    private String rejection_reason;

}
