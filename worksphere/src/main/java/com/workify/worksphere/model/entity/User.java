package com.workify.worksphere.model.entity;

import com.workify.worksphere.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    //TODO CHANGE TO EMBEDDEDID
    @GeneratedValue(strategy=GenerationType.UUID)
    private String userId;

    @Column(nullable = false)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable=false)
    private String jobTitle;

    @Column(nullable=false)
    private String department;

    private String profilePictureUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;
    //TODO Updated-at
}
