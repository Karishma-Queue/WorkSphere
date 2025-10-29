package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.Auth;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AuthRepository extends JpaRepository<Auth, UUID> {
        Optional<Auth> findByEmail(String email);

}
