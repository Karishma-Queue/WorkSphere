package com.karishma.worksphere.model.entity;

import com.karishma.worksphere.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID user_id;
    @Column(nullable = false)
    private String user_name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Column(nullable=false)
    private String job_title;
    @Column(nullable=false)
    private String department;
    private String profile_picture_url;
    @CreationTimestamp
    private LocalDateTime createdAt;


}
