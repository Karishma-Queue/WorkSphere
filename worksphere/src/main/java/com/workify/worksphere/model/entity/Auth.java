package com.workify.worksphere.model.entity;

import com.workify.worksphere.model.value.Email;
import com.workify.worksphere.model.valueobject.Email;
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

  @Embedded
  @AttributeOverride(name = "email", column = @Column(name = "email", nullable = false, unique = true))
  private Email email;

  @Column(name = "hashed_pass", nullable = false)
  private String hashedPass;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;
}