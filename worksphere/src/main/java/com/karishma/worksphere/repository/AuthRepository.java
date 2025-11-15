package com.karishma.worksphere.repository;

import com.karishma.worksphere.model.entity.Auth;
import com.karishma.worksphere.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AuthRepository extends JpaRepository<Auth, String> {
    Optional<Auth> findByEmail(String email);
    Optional<Auth> findByUser(User user);
}
