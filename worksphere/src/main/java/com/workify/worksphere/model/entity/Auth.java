package com.workify.worksphere.model.entity;

import com.workify.worksphere.model.value.AuthId;
import com.workify.worksphere.model.value.Email;
import com.workify.worksphere.model.value.AuthId;
import com.workify.worksphere.model.value.Email;
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
  @EmbeddedId
  private AuthId authId;

  @Embedded
  @AttributeOverride(name = "email", column = @Column(name = "email", nullable = false, unique = true))
  private Email email;

  @Column(name = "hashed_pass", nullable = false)
  private String hashedPass;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @PrePersist
  public void generateId() {
    if (this.authId == null) {
      this.authId = AuthId.generate();
    }
  }
}
