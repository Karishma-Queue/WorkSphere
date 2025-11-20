package com.workify.worksphere.repository;

import com.workify.worksphere.model.entity.Auth;
import com.workify.worksphere.model.entity.User;
import com.workify.worksphere.model.value.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AuthRepository extends JpaRepository<Auth, String> {
    Optional<Auth> findByEmail(Email email);
    Optional<Auth> findByUser(User user);
}
