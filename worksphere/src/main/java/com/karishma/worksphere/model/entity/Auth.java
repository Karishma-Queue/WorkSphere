package com.karishma.worksphere.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auth {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID auth_id;
    @Column(nullable=false,unique=true)
    private String email;
    @Column(nullable=false)
    private String hashed_pass;
    @OneToOne
    @JoinColumn(name="user_id")
    private User user;


}
