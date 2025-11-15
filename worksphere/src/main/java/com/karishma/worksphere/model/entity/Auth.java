package com.karishma.worksphere.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "auth_id")
    private String authId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "hashed_pass", nullable = false)
    private String hashedPass;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}